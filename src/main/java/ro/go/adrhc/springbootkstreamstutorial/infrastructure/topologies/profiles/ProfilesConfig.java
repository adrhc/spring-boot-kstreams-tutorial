package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

@Configuration
@Profile("!test")
@Slf4j
public class ProfilesConfig {
	private final TopicsProperties topicsProperties;

	public ProfilesConfig(TopicsProperties topicsProperties) {this.topicsProperties = topicsProperties;}

	/**
	 * Used by ExceedsConfig too.
	 */
	@Bean
	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getClientProfiles(),
				Consumed.as(topicsProperties.getClientProfiles() + "-consumer"),
				Materialized.as(topicsProperties.getClientProfiles() + "-store"));
	}

	@Bean
	public KStream<String, ClientProfile> profiles(StreamsBuilder streamsBuilder) {
		KTable<String, ClientProfile> clientProfileTable = clientProfileTable(streamsBuilder);
		KStream<String, ClientProfile> profiles = clientProfileTable.toStream();
		profiles.foreach((clientId, profile) -> log.debug("\n\t{}", profile));
		return profiles;
	}
}
