package ro.go.adrhc.springbootkstreamstutorial.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
@Data
@NoArgsConstructor
public class AppProperties {
	private String version;
}
