package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.amount;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.messages.Transaction;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

import static ro.go.adrhc.kafkastreamsextensions.streams.StreamsBuilderEx.from;

@Configuration
@Profile("!test")
@Slf4j
public class AmountExceededConfig {
	private final TopicsProperties topicsProperties;

	public AmountExceededConfig(TopicsProperties topicsProperties) {this.topicsProperties = topicsProperties;}

	@Bean
	public KStream<String, Transaction> transactions(StreamsBuilder pStreamsBuilder,
			KTable<String, ClientProfile> clientProfileTable) {
		StreamsBuilderEx streamsBuilder = from(pStreamsBuilder);
		KStreamEx<String, Transaction> transactions = transactionsStream(streamsBuilder);
		transactions
				.peek((id, t) -> log.debug("\n\tTransaction: {}", t))
				.join(clientProfileTable,
						this::amountExceededJoiner,
						Joined.as("txJoinProfiles"))
				.filter((k, v) -> v != null)
				.foreach((id, ae) -> log.debug("\n\t{}", ae));
		return transactions;
	}

	private AmountExceeded amountExceededJoiner(Transaction t, ClientProfile cp) {
		if (t.getAmount() <= cp.getAmountLimit()) {
			return null;
		}
		return new AmountExceeded(cp.getName(), cp.getSurname(), cp.getEmail(), cp.getPhone(),
				t.getTime(), t.getMerchantId(), t.getAmount(), cp.getAmountLimit());
	}

	private KStreamEx<String, Transaction> transactionsStream(StreamsBuilderEx streamsBuilder) {
		return streamsBuilder.stream(topicsProperties.getTransactions(),
				Consumed.as(topicsProperties.getTransactions()));
	}
}
