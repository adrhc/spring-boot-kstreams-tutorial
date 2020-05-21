package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;

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
	public KStream<byte[], Command> reportingCommands(StreamsBuilder streamsBuilder) {
		KStream<byte[], Command> commands = commandsStream(streamsBuilder);
		commands.foreach((k, command) -> {
			log.debug("\n\tcommand received: {}", command);
			log.debug("\n\tConfiguration:\n\t\tspring profiles = {}\n\t\tapp version = {}",
					env.getActiveProfiles(), appProperties.getVersion());
		}, Named.as("commandsReceiver"));
		return commands;
	}

	/**
	 * Wrapping the commands topic with KStream.
	 */
	private KStream<byte[], Command> commandsStream(StreamsBuilder streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getCommands(),
				Consumed.with(Serdes.ByteArray(), new JsonSerde<>(Command.class))
						.withName(topicsProperties.getCommands()));
	}
}
