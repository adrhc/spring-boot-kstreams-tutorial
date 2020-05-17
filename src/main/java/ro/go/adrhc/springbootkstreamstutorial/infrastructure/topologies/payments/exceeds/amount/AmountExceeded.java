package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.payments.exceeds.amount;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AmountExceeded implements Serializable {
	private java.lang.String name;
	private java.lang.String surname;
	private java.lang.String email;
	private java.lang.String phone;
	private java.time.LocalDate time;
	private java.lang.String merchantId;
	private int amount;
	private int amountLimit;
}
