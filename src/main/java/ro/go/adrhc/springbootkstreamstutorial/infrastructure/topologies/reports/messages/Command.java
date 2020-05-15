package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Command implements Serializable {
	private java.lang.String name;
	private java.util.List<java.lang.String> parameters;
}
