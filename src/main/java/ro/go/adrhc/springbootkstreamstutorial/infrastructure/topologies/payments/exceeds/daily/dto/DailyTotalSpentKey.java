package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.daily.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DailyTotalSpentKey implements Serializable {
	private String clientId;
	private LocalDate time;
}
