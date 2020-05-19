package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.operators.aggregation.LocalDateBasedKey;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;

import java.util.Comparator;
import java.util.List;

import static ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx.from;
import static ro.go.adrhc.springbootkstreamstutorial.util.DateUtils.format;
import static ro.go.adrhc.springbootkstreamstutorial.util.StreamsUtils.storeOf;

@Configuration
@Profile("!test")
@Slf4j
public class CommandsConfig {
	private final AppProperties appProperties;
	private final TopicsProperties topicsProperties;
	private final Environment env;

	public CommandsConfig(AppProperties appProperties, TopicsProperties topicsProperties, Environment env) {
		this.appProperties = appProperties;
		this.topicsProperties = topicsProperties;
		this.env = env;
	}

	/**
	 * Creating a sub/topology.
	 */
	@Bean
	@DependsOn("dailyTotalSpentTable")
	public KStream<byte[], Command> commands(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEx streamsBuilder = from(pStreamsBuilder);
		KStreamEx<byte[], Command> commands = commandsStream(streamsBuilder);

		// configuration report
		commands
				.filter((k, cmd) -> cmd.getNames().contains("config"))
				.foreach((k, c) -> log.debug("\n\tConfiguration:\n\t\tspring profiles = {}\n\t\tapp version = {}",
						env.getActiveProfiles(), appProperties.getVersion()));

		// clients profiles
		commands
				.filter((k, cmd) -> cmd.getNames().contains("profiles"))
				.<ClientProfile>allValuesOf(storeOf(topicsProperties.getClientProfiles())) // see Materialized.as in ProfilesConfig
				.foreach((k, profiles) -> profiles.forEach(profile -> log.debug("\n\tClient profiles:\n\t\t{}", profile)));

		// daily total spent report
		String dailyTotalSpentStore = storeOf(topicsProperties.getDailyTotalSpent());
		commands
				.filter((k, cmd) -> cmd.getNames().contains("daily"))
				// querying dailyTotalSpentStore to get a List<KeyValue<clientId-day, amount>>
				.<String, Integer>allOf(dailyTotalSpentStore)
				// The variable "list" below is a List<KeyValue<clientId-day, amount>>.
				.foreach((k, dailyTotalsList) -> this.logDailyTotals(dailyTotalsList));

		return commands;
	}

	private void logDailyTotals(List<KeyValue<String, Integer>> dailyTotals) {
		log.debug("\n\tDaily totals:");
		dailyTotals.stream()
				// "kv1" below is KeyValue<clientId-day, amount>
				// LocalDateBasedKey is a POJO containing the day and client-id.
				.map(kv1 -> KeyValue.pair(LocalDateBasedKey.parseWithStringData(kv1.key), kv1.value))
				// skipping the empty Optional<LocalDateBasedKey> (shouldn't ever happen)
				.filter(it1 -> it1.key.isPresent())
				// "kv2" below is KeyValue<Optional<LocalDateBasedKey>, Integer>
				.map(kv2 -> KeyValue.pair(kv2.key.get(), kv2.value))
				// "kv3" below is KeyValue<LocalDateBasedKey, Integer>
				// Sorting "dailyTotals" (variable above) by the expenditure's day.
				.sorted(Comparator.comparing(kv3 -> kv3.key.getTime()))
				// logging the total expenses for each day (sorted by day)
				.forEach(kv -> log.debug("\n\t\tClient (id) {} spent {} {} on {}", kv.key.getData(),
						kv.value, appProperties.getCurrency(), format(kv.key.getTime())));
	}

	@Bean
	public KTable<String, Integer> dailyTotalSpentTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getDailyTotalSpent(),
				Consumed.<String, Integer>
						as(topicsProperties.getDailyTotalSpent())
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()),
				Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>
						as(storeOf(topicsProperties.getDailyTotalSpent()))
						.withKeySerde(Serdes.String())
						.withValueSerde(Serdes.Integer()));
	}

	/**
	 * Wrapping the commands topic with KStream.
	 */
	private KStreamEx<byte[], Command> commandsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.with(Serdes.ByteArray(), new JsonSerde<>(Command.class))
						.withName(topicsProperties.getCommands() + "-consumer"));
	}
}
