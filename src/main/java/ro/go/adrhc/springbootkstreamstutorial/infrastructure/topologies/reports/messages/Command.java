package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Command implements Serializable {
	private java.lang.String name;
	private java.util.List<java.lang.String> parameters;
}
