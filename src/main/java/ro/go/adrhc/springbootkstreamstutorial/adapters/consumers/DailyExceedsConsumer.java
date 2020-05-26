package ro.go.adrhc.springbootkstreamstutorial.adapters.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springbootkstreamstutorial.adapters.phone.PhoneMessageSender;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.payments.exceeds.daily.messages.DailyExceeded;

@Profile("!test")
@Component
@Slf4j
public class DailyExceedsConsumer {
	private final PhoneMessageSender phoneMessageSender;

	public DailyExceedsConsumer(PhoneMessageSender phoneMessageSender) {this.phoneMessageSender = phoneMessageSender;}

	/**
	 * key.deserializer: https://kafka.apache.org/documentation/#key.deserializer
	 * value.deserializer: https://kafka.apache.org/documentation/#value.deserializer
	 * SpecificAvroDeserializer needs schema.registry.url.
	 * JsonDeserializer must use a certain type specified with spring.json.key.default.type.
	 */
	@KafkaListener(id = "dailyExceedsNotifier", topics = "${topic.daily-exceeds}",
			clientIdPrefix = "dailyExceedsConsumer", properties = {
			"schema.registry.url=${schema.registry.url}",
			"spring.json.trusted.packages=ro.go.adrhc.springbootkstreamstutorial" +
					".adapters.topologies.payments.exceeds.daily.dto",
			"key.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
			"value.deserializer=io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer",
			"spring.json.key.default.type=ro.go.adrhc.springbootkstreamstutorial" +
					".adapters.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey"})
	public void consume(DailyExceeded de) {
		phoneMessageSender.send(de);
	}
}
