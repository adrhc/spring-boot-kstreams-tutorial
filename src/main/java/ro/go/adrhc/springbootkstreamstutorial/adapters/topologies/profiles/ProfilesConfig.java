package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.profiles;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.profiles.messages.ClientProfile;

@Configuration
@Profile("!test")
@Slf4j
public class ProfilesConfig {
	private final TopicsProperties topicsProperties;

	public ProfilesConfig(TopicsProperties topicsProperties) {this.topicsProperties = topicsProperties;}

	/**
	 * Used by ExceedsConfig too.
	 *
	 * KTable(client-profiles)
	 * 
	 * [clientId1, ClientProfile1-v1]
	 * [clientId1, ClientProfile1-v2]
	 * [clientId1, ClientProfile1-v3] -> last value
	 *
	 * KStream(transactions) - join - KTable(client-profiles)
	 * [txId, transaction-clientId1] join last client-profiles value
	 * [txId, transaction-clientId1] join last client-profiles value
	 *
	 * KStream(transactions) - join - KStream(client-profiles)
	 * time period: e.g. 1d
	 * [txId, transaction-clientId1] join last ClientProfile1-v1 value
	 * [txId, transaction-clientId1] join last ClientProfile1-v2 value
	 *
	 * client-profiles: key and value
	 */
	@Bean
	public KTable<String, ClientProfile> clientProfileTable(StreamsBuilder streamsBuilder) {
		return streamsBuilder.table(topicsProperties.getClientProfiles(),
				Consumed.as(topicsProperties.getClientProfiles() + "-consumer"),
				Materialized.as(topicsProperties.getClientProfiles() + "-store"));
	}

	/**
	 * KStream(client-profiles)
	 *
	 * [clientId1, ClientProfile1-v1] -> business logic called
	 * [clientId1, ClientProfile1-v2] -> business logic called
	 * [clientId1, ClientProfile1-v3] -> business logic called
	 *
	 * Kafka broker (client-profiles) -> clientProfileTable -> foreach
	 */
	@Bean
	public KStream<String, ClientProfile> profiles(StreamsBuilder streamsBuilder) {
		KTable<String, ClientProfile> clientProfileTable = clientProfileTable(streamsBuilder);
		KStream<String, ClientProfile> profiles = clientProfileTable.toStream();
		profiles.foreach((clientId, profile) -> log.debug("\n\t{}", profile));
		return profiles;
	}
}
