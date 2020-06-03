package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

import static ro.go.adrhc.springbootkstreamstutorial.util.StreamsUtils.streamOf;

@Component
@DependsOn({"dailyTotalSpentTable", "clientProfileTable"})
@Profile("!test")
@Slf4j
public class DailyExceedsConfig extends AbstractExceeds {
	private final KTable<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentTable;
	private final KTable<String, ClientProfile> clientProfileTable;
	private final AppProperties appProperties;

	public DailyExceedsConfig(TopicsProperties topicsProperties, KTable<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentTable, KTable<String, ClientProfile> clientProfileTable, AppProperties appProperties) {
		super(topicsProperties);
		this.dailyTotalSpentTable = dailyTotalSpentTable;
		this.clientProfileTable = clientProfileTable;
		this.appProperties = appProperties;
	}

	public void accept() {
		dailyTotalSpentTable
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						DailyTotalSpent::getClientId,
						this::dailyExceededJoiner,
						Named.as("dailyTotalSpentJoinClientProfile"))
				// skip for less than dailyMaxAmount
				.filter((dailyTotalSpentKey, dailyExceeded) -> dailyExceeded != null)
				.toStream(Named.as(streamOf(topicsProperties.getDailyExceeds())))
				.foreach((dailyTotalSpentKey, dailyExceeded) -> log.debug("\n\t{}", dailyExceeded));
	}

	private DailyExceeded dailyExceededJoiner(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			return new DailyExceeded(cp.getDailyMaxAmount(), dts);
		}
		log.trace("\n\tskipping daily total spent under {} {}\n\t{}\n\t{}",
				cp.getDailyMaxAmount(), appProperties.getCurrency(), dts, cp);
		return null;
	}
}
