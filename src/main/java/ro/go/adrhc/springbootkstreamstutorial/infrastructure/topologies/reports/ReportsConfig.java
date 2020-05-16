package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;

import static ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx.extend;

@Configuration
@Profile("!test")
@Slf4j
public class ReportsConfig {
	private final AppProperties appProperties;
	private final TopicsProperties topicsProperties;
	private final Environment env;

	public ReportsConfig(AppProperties appProperties, TopicsProperties topicsProperties, Environment env) {
		this.appProperties = appProperties;
		this.topicsProperties = topicsProperties;
		this.env = env;
	}

	/**
	 * Creating a sub/topology.
	 */
	@Bean
	public KStream<byte[], Command> reportingCommands(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEx streamsBuilder = extend(pStreamsBuilder);
		KStreamEx<byte[], Command> commands = commandsStream(streamsBuilder);
		// configuration report
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("config"))
				.foreach((k, c) -> log.debug("\n\tConfiguration:\n\t\tspring profiles = {}\n\t\tapp version = {}",
						env.getActiveProfiles(), appProperties.getVersion()));
		// clients profiles
		commands
				.filter((k, cmd) -> cmd.getParameters().contains("profiles"))
				.<ClientProfile>allOf(topicsProperties.getClientProfiles() + "-store") // see Materialized.as in ProfilesConfig
				.foreach((k, profiles) -> profiles.forEach(profile -> log.debug("\n\tClient profiles:\n\t\t{}", profile)));
		return commands;
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
