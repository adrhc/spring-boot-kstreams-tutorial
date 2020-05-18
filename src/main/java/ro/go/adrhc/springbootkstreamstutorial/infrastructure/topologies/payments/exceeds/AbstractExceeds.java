package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds;

import org.apache.kafka.streams.kstream.Consumed;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.messages.Transaction;

public abstract class AbstractExceeds {
	protected final TopicsProperties topicsProperties;

	protected AbstractExceeds(TopicsProperties topicsProperties) {this.topicsProperties = topicsProperties;}

	protected KStreamEx<String, Transaction> transactionsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getTransactions(),
				Consumed.as(topicsProperties.getTransactions()));
	}
}
