package ro.go.adrhc.springbootkstreamstutorial.infrastructure.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.phone.PhoneMessageSender;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.messages.DailyExceeded;

@Profile("!test")
@Component
@Slf4j
public class DailyExceedsConsumer {
	private final PhoneMessageSender phoneMessageSender;

	public DailyExceedsConsumer(PhoneMessageSender phoneMessageSender) {this.phoneMessageSender = phoneMessageSender;}

	@KafkaListener(id = "dailyExceedsNotifier", topics = "${topic.daily-exceeds}",
			clientIdPrefix = "dailyExceedsConsumer", properties = {
			"schema.registry.url=${schema.registry.url}",
			"spring.json.trusted.packages=ro.go.adrhc.springbootkstreamstutorial" +
					".infrastructure.topologies.payments.exceeds.daily.dto",
			"key.deserializer=org.springframework.kafka.support.serializer.JsonDeserializer",
			"value.deserializer=io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer",
			"spring.json.key.default.type=ro.go.adrhc.springbootkstreamstutorial" +
					".infrastructure.topologies.payments.exceeds.daily.dto.DailyTotalSpentKey"})
	public void consume(DailyExceeded de) {
		phoneMessageSender.send(de);
	}
}
