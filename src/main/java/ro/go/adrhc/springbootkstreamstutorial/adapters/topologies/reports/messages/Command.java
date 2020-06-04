package ro.go.adrhc.springbootkstreamstutorial.adapters.topologies.reports.messages;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Command implements Serializable {
	private java.util.List<java.lang.String> names;
}
