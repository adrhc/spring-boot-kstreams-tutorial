package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

@Component
@DependsOn({"dailyTotalSpentTable", "clientProfileTable"})
@Profile("!test")
@Slf4j
public class DailyExceedsConfig extends AbstractExceeds {
	private final KTable<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentTable;
	private final KTable<String, ClientProfile> clientProfileTable;
	private final JsonSerde<DailyTotalSpentKey> dailyTotalSpentKeySerde;
	private final AppProperties appProperties;

	public DailyExceedsConfig(TopicsProperties topicsProperties, KTable<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentTable, KTable<String, ClientProfile> clientProfileTable, JsonSerde<DailyTotalSpentKey> dailyTotalSpentKeySerde, AppProperties appProperties) {
		super(topicsProperties);
		this.dailyTotalSpentTable = dailyTotalSpentTable;
		this.clientProfileTable = clientProfileTable;
		this.dailyTotalSpentKeySerde = dailyTotalSpentKeySerde;
		this.appProperties = appProperties;
	}

	public void get() {
		dailyTotalSpentTable
				// changing the key to the client-id
				.toStream((dailyTotalSpentKey, dailyTotalSpent) -> dailyTotalSpentKey.getClientId())
				// clientId:DailyTotalSpent join clientId:ClientProfile
				.join(clientProfileTable,
						this::dailyExceededJoiner,
						dailyTotalSpentJoinClientProfile())
				// skip for less than dailyMaxAmount
				.filter((clientId, dailyExceeded) -> dailyExceeded != null)
				// logging the daily exceeds
				.peek((clientId, dailyExceeded) -> log.debug("\n\t{}", dailyExceeded))
				.map(this::dailyTotalSpentKey)
				.to(topicsProperties.getDailyExceeds(), produceDailyExceeded());
	}

	private KeyValue<DailyTotalSpentKey, DailyExceeded> dailyTotalSpentKey(String clientId, DailyExceeded dailyExceeded) {
		return KeyValue.pair(new DailyTotalSpentKey(clientId,
				dailyExceeded.getDailyTotalSpent().getTime()), dailyExceeded);
	}

	private DailyExceeded dailyExceededJoiner(DailyTotalSpent dts, ClientProfile cp) {
		if (cp.getDailyMaxAmount() < dts.getAmount()) {
			// *.avpr does not support imports but *.avdl does
			return new DailyExceeded(cp.getDailyMaxAmount(), dts, cp.getName(), cp.getSurname(), cp.getEmail(), cp.getPhone());
		}
		log.trace("\n\tskipping daily total spent under {} {}\n\t{}\n\t{}",
				cp.getDailyMaxAmount(), appProperties.getCurrency(), dts, cp);
		return null;
	}

	private Joined<String, DailyTotalSpent, ClientProfile> dailyTotalSpentJoinClientProfile() {
		return Joined.as("dailyTotalSpentJoinClientProfile");
	}

	private Produced<DailyTotalSpentKey, DailyExceeded> produceDailyExceeded() {
		return Produced.<DailyTotalSpentKey, DailyExceeded>
				as(topicsProperties.getDailyExceeds())
				.withKeySerde(dailyTotalSpentKeySerde);
	}
}
