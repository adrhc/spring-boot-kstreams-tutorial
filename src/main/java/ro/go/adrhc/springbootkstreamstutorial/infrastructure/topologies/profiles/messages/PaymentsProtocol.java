/**
 * Autogenerated by Avro
 * <p>
 * DO NOT EDIT DIRECTLY
 */
package ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages;

/** payments protocol */
@org.apache.avro.specific.AvroGenerated
public interface PaymentsProtocol {
	org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"PaymentsProtocol\",\"namespace\":\"ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages\",\"doc\":\"payments protocol\",\"types\":[{\"type\":\"record\",\"name\":\"ClientProfile\",\"fields\":[{\"name\":\"id\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"surname\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"email\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"phone\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"dailyMaxAmount\",\"type\":\"int\"},{\"name\":\"periodMaxAmount\",\"type\":\"int\"}]}],\"messages\":{}}");

	@SuppressWarnings("all")
	/** payments protocol */
	public interface Callback extends PaymentsProtocol {
		public static final org.apache.avro.Protocol PROTOCOL = ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.profiles.messages.PaymentsProtocol.PROTOCOL;
	}
}
