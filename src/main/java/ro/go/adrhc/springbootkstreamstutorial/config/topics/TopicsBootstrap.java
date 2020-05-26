package ro.go.adrhc.springbootkstreamstutorial.config.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;
import ro.go.adrhc.springbootkstreamstutorial.adapters.config.TopicsProperties;

@Configuration
@Profile("!test")
public class TopicsBootstrap {
	private final TopicsProperties properties;

	public TopicsBootstrap(TopicsProperties properties) {
		this.properties = properties;
	}

	@Bean
	public NewTopic commandTopic() {
		return TopicBuilder.name(properties.getCommands()).build();
	}

	@Bean
	public NewTopic clientProfileTopic() {
		return TopicBuilder.name(properties.getClientProfiles()).compact().build();
	}

	@Bean
	public NewTopic transactionsTopic() {
		return TopicBuilder.name(properties.getTransactions()).build();
	}

	@Bean
	public NewTopic dailyTotalSpentTopic() {
		return TopicBuilder.name(properties.getDailyTotalSpent()).compact().build();
	}

	@Bean
	public NewTopic dailyExceededTopic() {
		return TopicBuilder.name(properties.getDailyExceeds()).compact().build();
	}
}
