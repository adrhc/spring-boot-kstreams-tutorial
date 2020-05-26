package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.reports;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonSerde;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.adapters.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyTotalSpent;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.profiles.messages.ClientProfile;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.reports.messages.Command;

import java.util.List;

import static java.util.Comparator.comparing;
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
				.<DailyTotalSpent>allValuesOf(dailyTotalSpentStore)
				// "dailyTotalsList" below is a List<KeyValue<clientId-day, amount>>
				.foreach((k, dailyTotalsList) -> this.logDailyTotals(dailyTotalsList));

		return commands;
	}

	private void logDailyTotals(List<DailyTotalSpent> dailyTotals) {
		log.debug("\n\tDaily totals:");
		dailyTotals.stream()
				// sorting "dailyTotals" (variable above) by the expenditure's day
				.sorted(comparing(DailyTotalSpent::getTime))
				// logging the total expenses for each day (sorted by day)
				.forEach(dts -> log.debug("\n\t\tClient (id) {} spent {} {} on {}", dts.getClientId(),
						dts.getAmount(), appProperties.getCurrency(), format(dts.getTime())));
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
