package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.messages.Transaction;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.aggregation.LocalDateBasedKey.keyOf;
import static ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.util.DateUtils.localDateTimeOf;
import static ro.go.adrhc.springbootkstreamstutorial.util.DateUtils.format;

@Component
@Profile("!test")
@Slf4j
public class DailyTotalsConfig extends AbstractExceeds {
	private final AppProperties appProperties;

	public DailyTotalsConfig(TopicsProperties topicsProperties, AppProperties appProperties) {
		super(topicsProperties);
		this.appProperties = appProperties;
	}

	protected static Materialized<String, Integer, WindowStore<Bytes, byte[]>> dailyTotalSpentByClientId(
			int retentionDays, int windowSize, ChronoUnit windowUnit) {
		return Materialized.<String, Integer, WindowStore<Bytes, byte[]>>
				as("dailyTotalSpentByClientId-" + windowSize + windowUnit.toString())
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
						dailyTotalSpentByClientId(appProperties.getDailyGrace() + 1, 1, DAYS))
				// computes a new key (i.e. clientId-day) for each record e.g. 1-2020.05.02
				.toStream((win, amount) -> keyOf(win))
				// saves clientId-day:amount record into a compact stream (aka table)
				.to(topicsProperties.getDailyTotalSpent());
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
