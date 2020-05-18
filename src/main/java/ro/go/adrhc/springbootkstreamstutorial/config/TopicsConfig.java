package ro.go.adrhc.springbootkstreamstutorial.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
public class TopicsConfig {
	private final TopicsProperties properties;

	public TopicsConfig(TopicsProperties properties) {
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
