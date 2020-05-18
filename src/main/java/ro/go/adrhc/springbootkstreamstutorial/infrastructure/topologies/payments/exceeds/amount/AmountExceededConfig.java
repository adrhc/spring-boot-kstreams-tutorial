package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.amount;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ro.go.adrhc.kafkastreamsextensions.streams.kstream.KStreamEx;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.AbstractExceeds;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.messages.Transaction;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

@Component
@Profile("!test")
@Slf4j
public class AmountExceededConfig extends AbstractExceeds {
	private final KTable<String, ClientProfile> clientProfileTable;

	public AmountExceededConfig(TopicsProperties topicsProperties, KTable<String, ClientProfile> clientProfileTable) {
		super(topicsProperties);
		this.clientProfileTable = clientProfileTable;
	}

	public void accept(KStreamEx<String, Transaction> transactions) {
		transactions
				.peek((id, t) -> log.debug("\n\tTransaction: {}", t))
				.join(clientProfileTable,
						this::amountExceededJoiner,
						Joined.as("txJoinProfiles"))
				.filter((k, v) -> v != null)
				.foreach((id, ae) -> log.debug("\n\t{}", ae));
	}

	private AmountExceeded amountExceededJoiner(Transaction t, ClientProfile cp) {
		if (t.getAmount() <= cp.getAmountLimit()) {
			return null;
		}
		return new AmountExceeded(cp.getName(), cp.getSurname(), cp.getEmail(), cp.getPhone(),
				t.getTime(), t.getMerchantId(), t.getAmount(), cp.getAmountLimit());
	}
}
