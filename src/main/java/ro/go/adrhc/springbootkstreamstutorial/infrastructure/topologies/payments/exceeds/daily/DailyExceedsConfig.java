package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.aggregation.LocalDateBasedKey.parseWithStringData;
import static ro.go.adrhc.springbootkstreamstutorial.util.DateUtils.format;
import static ro.go.adrhc.springbootkstreamstutorial.util.StreamsUtils.streamOf;

/*
@Component
@DependsOn({"dailyTotalSpentTable", "clientProfileTable"})
@Profile("!test")
*/
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

/*
	public void get() {
		dailyTotalSpentTable
				.toStream()
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						this::dailyExceededJoiner,
						dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// clientId:DailyExceeded streams
//				.foreach((clientId, dailyExceeded) ->
//						log.debug("\n\tclientId: {}\n\t{}", clientId, dailyExceeded));
				.to(topicsProperties.getDailyExceeds(), produceDailyExceeded());
	}
*/

	private Produced<String, DailyExceeded> produceDailyExceeded() {
		return Produced.as(streamOf(topicsProperties.getDailyExceeds()));
	}

	private Joined<String, DailyTotalSpent, ClientProfile> dailyTotalSpentJoinClientProfile() {
		return Joined.as("dailyTotalSpentJoinClientProfile");
	}

	private DailyExceeded dailyExceededJoiner(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			return new DailyExceeded(cp.getDailyMaxAmount(), dts);
		}
		log.trace("\n\tskipping daily total spent under {} {}\n\t{}\n\t{}",
				cp.getDailyMaxAmount(), appProperties.getCurrency(), dts, cp);
		return null;
	}

	private KeyValue<String, DailyTotalSpent> clientIdDailyTotalSpentOf(String clientIdDay, Integer amount) {
		return parseWithStringData(clientIdDay)
				.map(it -> {
					String clientId = it.getData();
					log.debug("\n\t{} spent a total of {} {} on {}",
							clientId, amount, appProperties.getCurrency(), format(it.getTime()));
					return KeyValue.pair(clientId, new DailyTotalSpent(clientId, it.getTime(), amount));
				})
				.orElse(null);
	}
}
