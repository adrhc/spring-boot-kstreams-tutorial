package ro.go.adrhc.springbootkstreamstutorial.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@EnableKafka
@EnableKafkaStreams
@Configuration
public class TopicsConfig {
	private final TopicsProperties properties;

	public TopicsConfig(TopicsProperties properties) {
		this.properties = properties;
	}

	@Bean
	public NewTopic commandTopic() {
		return TopicBuilder.name(properties.getCommands()).build();
	}
}
