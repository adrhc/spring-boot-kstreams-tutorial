package ro.go.adrhc.springbootkstreamstutorial.adapters.consumers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.reports.messages.Command;

@Profile("!test")
@Component
@Slf4j
public class CommandConsumer {
	@KafkaListener(id = "commandConsumer", topics = "${topic.commands}",
			clientIdPrefix = "commandConsumer", properties = {"spring.json.value.default.type=" +
			"ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.reports.messages.Command"})
	public void consume(Command command) {
		log.debug("\n\tConsumed: {}", command);
//		throw new RuntimeException("by default the consumer retries on error " + LocalDateTime.now());
	}
}
