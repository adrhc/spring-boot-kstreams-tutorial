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
	/*
	 * this should pertain to "adapters"
	 * could be used by monitoring, reporting, logging, audit
	 */
	private String version;
	/*
	 * This should be loaded from a store along with amounts spent.
	 * If configured than should pertain to application's "core" if is
	 * part of the domain-model or "adapters" if related only to reporting.
	 */
	private String currency;
	/*
	 * This is related to topology configuration so should be in
	 * something named like e.g. ...adapters.config.TopologiesProperties.
	 * Could also pertain to domain-model in which case should stay in application's "core".
	 */
	private int dailyGrace;
}
