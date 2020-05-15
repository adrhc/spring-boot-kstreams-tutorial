package ro.go.adrhc.springbootkstreamstutorial.infrastructure.consumers;

import org.springframework.kafka.support.serializer.JsonDeserializer;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;

public class CommandDeserializer extends JsonDeserializer<Command> {
	public CommandDeserializer() {
		super(Command.class);
	}
}
