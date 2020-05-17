package ro.go.adrhc.springbootkstreamstutorial.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ro.go.adrhc.springbootkstreamstutorial.infrastructure.topologies.reports.messages.Command;

import java.util.Map;

@TestConfiguration
public class KafkaTemplateConfig {
	private final KafkaProperties properties;
	private final String schemaRegistryUrl;

	public KafkaTemplateConfig(KafkaProperties properties,
			@Value("${schema.registry.url}") String schemaRegistryUrl) {
		this.properties = properties;
		this.schemaRegistryUrl = schemaRegistryUrl;
	}

	@Bean
	public KafkaTemplate<String, Integer> intKTemplate(
			ObjectProvider<RecordMessageConverter> messageConverter) {
		return kafkaTemplateImpl(properties.getProducer().getClientId() + "-int",
				new IntegerSerializer(), messageConverter);
	}

	@Bean
	public KafkaTemplate<String, Object> avroKTemplate(
			ObjectProvider<RecordMessageConverter> messageConverter) {
		KafkaAvroSerializer valueSerializer = new KafkaAvroSerializer();
		valueSerializer.configure(Map.of("schema.registry.url", schemaRegistryUrl), false);
		return kafkaTemplateImpl(properties.getProducer().getClientId() + "-avro",
				valueSerializer, messageConverter);
	}

	@Bean
	public KafkaTemplate<String, Command> commandKTemplate(
			ObjectProvider<RecordMessageConverter> messageConverter) {
		return kafkaTemplateImpl(properties.getProducer().getClientId() + "-command",
				new JsonSerializer<>(), messageConverter);
	}

	private <V> KafkaTemplate<String, V> kafkaTemplateImpl(
			String clientIdPrefix, Serializer<V> valueSerializer,
			ObjectProvider<RecordMessageConverter> messageConverter) {
		// producer config
		Map<String, Object> config = this.properties.buildProducerProperties();
		config.put(ProducerConfig.CLIENT_ID_CONFIG, clientIdPrefix + "-producer");
		// producer factory
		DefaultKafkaProducerFactory<String, V> factory = new DefaultKafkaProducerFactory<>(config);
		factory.setValueSerializer(valueSerializer);
		String transactionIdPrefix = this.properties.getProducer().getTransactionIdPrefix();
		if (transactionIdPrefix != null) {
			factory.setTransactionIdPrefix(transactionIdPrefix);
		}
		// KafkaTemplate
		KafkaTemplate<String, V> kafkaTemplate = new KafkaTemplate<>(factory);
		messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
		kafkaTemplate.setProducerListener(new LoggingProducerListener<>());
		kafkaTemplate.setDefaultTopic(this.properties.getTemplate().getDefaultTopic());
		return kafkaTemplate;
	}
}
