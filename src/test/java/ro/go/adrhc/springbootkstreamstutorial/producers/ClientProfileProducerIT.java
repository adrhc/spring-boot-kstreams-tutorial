package ro.go.adrhc.springbootkstreamstutorial.producers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import ro.go.adrhc.springbootkstreamstutorial.config.KafkaTemplateConfig;
import ro.go.adrhc.springbootkstreamstutorial.config.TopicsProperties;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.ClientProfile;

import java.util.function.IntSupplier;

import static ro.go.adrhc.springbootkstreamstutorial.producers.TransactionsProducerIT.MAX_AMOUNT;

@EnabledIfSystemProperty(named = "enableIT", matches = "true")
@ActiveProfiles({"test"})
@Import(KafkaTemplateConfig.class)
@SpringBootTest
@Slf4j
public class ClientProfileProducerIT {
	private static final IntSupplier MAX_DAILY_AMOUNT_SUPP =
			() -> RandomUtils.nextInt(50, 101);
	private static final IntSupplier MAX_PERIOD_AMOUNT_SUPP =
			() -> RandomUtils.nextInt(150, 301);
	@Autowired
	@Qualifier("avroKTemplate")
	private KafkaTemplate<String, Object> avroKTemplate;
	@Autowired
	private TopicsProperties properties;
	@Autowired
	private Environment env;

	private static ClientProfile randomClientProfile() {
		return new ClientProfile("1", "Gigi", "Kent",
				"gigikent@candva.ro", "0720000000", MAX_AMOUNT / 2,
				MAX_DAILY_AMOUNT_SUPP.getAsInt(), MAX_PERIOD_AMOUNT_SUPP.getAsInt());
	}

	@Test
	void sendClientProfile() {
		log.debug("profiles: {}", String.join(", ", env.getActiveProfiles()));
		log.debug("ClientProfile topic: {}", properties.getClientProfiles());
		ClientProfile clientProfile = randomClientProfile();
		log.debug("clientProfile:\n\t{}", clientProfile);
		avroKTemplate.send(properties.getClientProfiles(), clientProfile.getId(), clientProfile);
	}
}
