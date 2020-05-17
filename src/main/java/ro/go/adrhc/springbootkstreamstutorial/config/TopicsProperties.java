package ro.go.adrhc.springbootkstreamstutorial.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("topic")
@Data
@NoArgsConstructor
public class TopicsProperties {
	// used as stream/destination topic name, Consumed.processorName
	private String commands;
	// used as destination topic name, KTable topic name, Consumed.processorName, Materialized.storeName
	private String clientProfiles;
	// used as stream/destination topic name, Consumed.processorName
	private String transactions;
}
