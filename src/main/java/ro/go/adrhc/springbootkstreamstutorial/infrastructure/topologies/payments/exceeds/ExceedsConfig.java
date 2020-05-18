package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.amount.AmountExceededConfig;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.DailyExceedsConfig;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.messages.Transaction;

import static ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx.from;

@Configuration
@Profile("!test")
@Slf4j
public class ExceedsConfig extends AbstractExceeds {
	private final AmountExceededConfig amountExceededConfig;
	private final DailyExceedsConfig dailyExceedsConfig;

	protected ExceedsConfig(TopicsProperties topicsProperties, AmountExceededConfig amountExceededConfig, DailyExceedsConfig dailyExceedsConfig) {
		super(topicsProperties);
		this.amountExceededConfig = amountExceededConfig;
		this.dailyExceedsConfig = dailyExceedsConfig;
	}

	@Bean
	public KStream<String, Transaction> transactions(StreamsBuilder pStreamsBuilder) {
		StreamsBuilderEx streamsBuilder = from(pStreamsBuilder);
		KStreamEx<String, Transaction> transactions = transactionsStream(streamsBuilder);
		amountExceededConfig.accept(transactions);
		dailyExceedsConfig.accept(transactions);
		return transactions;
	}

	protected KStreamEx<String, Transaction> transactionsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getTransactions(),
				Consumed.as(topicsProperties.getTransactions()));
	}
}