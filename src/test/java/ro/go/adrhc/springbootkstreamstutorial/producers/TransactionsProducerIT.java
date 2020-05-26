package ro.go.adrhc.springbootkstreamstutorial.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springbootkstreamstutorial.config.AppProperties;
import ro.go.adrhc.springbootkstreamstutorial.config.KafkaTemplateConfig;
import ro.go.adrhc.springbootkstreamstutorial.adapters.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.messages.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.DAYS;

@EnabledIfSystemProperty(named = "enableIT", matches = "true")
@ActiveProfiles({"test"})
@Import(KafkaTemplateConfig.class)
@SpringBootTest
@Slf4j
public class TransactionsProducerIT {
	public static final int MAX_AMOUNT = 10;
	@Autowired
	@Qualifier("avroKTemplate")
	private KafkaTemplate<String, Object> avroKTemplate;
	@Autowired
	private AppProperties app;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	public static Transaction randomTransaction(int maxIncludedDaysBeforeNow) {
		Instant randomInstant = Instant.now().minus(
				RandomUtils.nextInt(1, maxIncludedDaysBeforeNow + 1), DAYS);
		LocalDate ldt = LocalDate.ofInstant(randomInstant, ZoneOffset.UTC);
		return new Transaction(ldt, "1", "1", RandomUtils.nextInt(1, MAX_AMOUNT + 1));
	}

	@RepeatedTest(1)
	@DisabledIfSystemProperty(named = "transactionsCount", matches = "\\d+")
	void send() {
		log.debug("\n\tprofiles: {}", String.join(" + ", env.getActiveProfiles()));
		log.debug("\n\ttransactions topic: {}", properties.getTransactions());
		Transaction transaction = randomTransaction(app.getDailyGrace());
		log.debug("transaction:\n\t{}", transaction);
		avroKTemplate.send(properties.getTransactions(), transaction.getClientId(), transaction);
	}

	@Test
	@EnabledIfSystemProperty(named = "transactionsCount", matches = "\\d+")
	void sendMany() {
		int transactionsCount = transactionsCount();
		log.debug("\n\ttransactionsCount = {}", transactionsCount);
		IntStream.range(0, transactionsCount).forEach(i -> send());
	}

	private int transactionsCount() {
		String transactionsCount = System.getProperty("transactionsCount");
		if (transactionsCount == null) {
			return 1;
		}
		return parseInt(transactionsCount);
	}
}
