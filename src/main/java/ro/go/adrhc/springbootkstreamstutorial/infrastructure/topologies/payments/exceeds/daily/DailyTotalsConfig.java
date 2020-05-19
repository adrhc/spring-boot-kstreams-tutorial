package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.messages.Transaction;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.util.DateUtils.localDateOf;
import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.util.DateUtils.localDateTimeOf;
import static ro.go.adrhc.springbootkstreamstutorial.util.DateUtils.format;
import static ro.go.adrhc.springbootkstreamstutorial.util.StreamsUtils.*;

@Configuration
@Profile("!test")
@Slf4j
public class DailyTotalsConfig extends AbstractExceeds {
	private final AppProperties appProperties;

	public DailyTotalsConfig(TopicsProperties topicsProperties, AppProperties appProperties) {
		super(topicsProperties);
		this.appProperties = appProperties;
	}

	protected Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(int retentionDays) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentGroupedByClientId-1" + DAYS.toString())
				.withKeySerde(Serdes.String())
				.withValueSerde(Serdes.Integer())
				.withRetention(Duration.ofDays(retentionDays));
	}

	/**
	 * calculating total expenses per day
	 * using Tumbling time window
	 */
	public void accept(KStreamEx<String, Transaction> transactions) {
		// expenses grouped by client-id
		KGroupedStream<String, Transaction> txGroupedByCli = txGroupedByClientId(transactions);

		txGroupedByCli
				// expenses grouped by per 1 day
				.windowedBy(TimeWindows.of(Duration.ofDays(1))
						.grace(Duration.ofDays(appProperties.getDailyGrace())))
				// aggregate amount spent by a client per day
				.aggregate(() -> 0, (k, v, sum) -> sum + v.getAmount(),
						dailyTotalSpentByClientId(appProperties.getDailyGrace() + 1))
				/*
				 * win1.window().end()).minusDays(1) is an "including" date.
				 * Mapping to DailyTotalSpent.
				 */
				.mapValues((win1, amount) -> new DailyTotalSpent(win1.key(),
						localDateOf(win1.window().end()).minusDays(1), amount))
				// computes a new key for the new stream (dailyTotalSpent)
				.toStream((win2, dts) -> new DailyTotalSpentKey(dts.getClientId(), dts.getTime()))
				// saves clientId-day:amount record into a compact stream (aka table)
				.to(topicsProperties.getDailyTotalSpent(), dailyTotalSpentProducer());
	}

	private Produced<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentProducer() {
		return Produced.<DailyTotalSpentKey, DailyTotalSpent>
				as(streamOf(topicsProperties.getDailyTotalSpent()))
				// mixing AVRO (check application.yml for default.value.serde) with Spring's JsonSerde
				.withKeySerde(dailyTotalSpentKeySerde());
	}

	@Bean
	public KTable<DailyTotalSpentKey, DailyTotalSpent> dailyTotalSpentTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getDailyTotalSpent(),
				Consumed.<DailyTotalSpentKey, DailyTotalSpent>
						as(tableOf(topicsProperties.getDailyTotalSpent()))
						// mixing AVRO (check application.yml for default.value.serde) with Spring's JsonSerde
						.withKeySerde(dailyTotalSpentKeySerde()),
				Materialized.as(storeOf(topicsProperties.getDailyTotalSpent())));
	}

	@Bean
	public JsonSerde<DailyTotalSpentKey> dailyTotalSpentKeySerde() {
		return new JsonSerde<>(DailyTotalSpentKey.class);
	}

	/**
	 * group transactions by client-id
	 */
	private KGroupedStream<String, Transaction> txGroupedByClientId(
			KStreamEx<String, Transaction> transactions) {
		return transactions
				// log expenses using a custom peek implementation
				.peek(it -> {
					log.trace("\n\ttopic: {}\n\ttimestamp: {}",
							it.context.topic(), localDateTimeOf(it.context.timestamp()));
					log.debug("\n\tClient (id) {} spent {} {} on {}", it.key, it.value.getAmount(),
							appProperties.getCurrency(), format(it.value.getTime()));
					it.context.headers().forEach(h -> log.trace(h.toString()));
				})
				// the key is the client-id
				.groupByKey(Grouped.as("transactionsGroupedByClientId"));
	}
}
