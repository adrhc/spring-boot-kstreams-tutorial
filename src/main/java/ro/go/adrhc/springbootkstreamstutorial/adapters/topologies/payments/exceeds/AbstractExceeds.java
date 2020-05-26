package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds;

import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;

public abstract class AbstractExceeds {
	protected final TopicsProperties topicsProperties;

	protected AbstractExceeds(TopicsProperties topicsProperties) {this.topicsProperties = topicsProperties;}
}
