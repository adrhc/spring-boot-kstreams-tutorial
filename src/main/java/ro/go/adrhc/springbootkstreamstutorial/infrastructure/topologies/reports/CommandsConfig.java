package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
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
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.transformers.DailyValueTransformerSupp;

import java.util.Comparator;
import java.util.stream.Collectors;

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
				.<ClientProfile>allOf(storeOf(topicsProperties.getClientProfiles())) // see Materialized.as in ProfilesConfig
				.foreach((k, profiles) -> profiles.forEach(profile -> log.debug("\n\tClient profiles:\n\t\t{}", profile)));

		// daily total spent report
		String dailyTotalSpentStore = storeOf(topicsProperties.getDailyTotalSpent());
		commands
				.filter((k, cmd) -> cmd.getNames().contains("daily"))
				.transformValues(new DailyValueTransformerSupp(dailyTotalSpentStore), dailyTotalSpentStore)
				.foreach((k, list) -> {
					list.sort(Comparator.comparing(DailyTotalSpent::getTime));
					log.debug("\n\tDaily totals:{}", list.stream().map(it ->
							"\n\t\tClient (id) " + it.getClientId() + ", " + format(it.getTime()) +
									": " + it.getAmount() + " " + appProperties.getCurrency())
							.collect(Collectors.joining("\n\t")));
				});

		return commands;
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
