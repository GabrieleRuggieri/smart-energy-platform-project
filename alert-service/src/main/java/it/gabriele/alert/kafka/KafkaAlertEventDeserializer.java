package it.gabriele.alert.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gabriele.alert.model.BaseAlertEvent;
import it.gabriele.alert.model.BrazilAlertEvent;
import it.gabriele.alert.model.LstmAlertEvent;
import it.gabriele.alert.model.TestAlertEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class KafkaAlertEventDeserializer implements Deserializer<BaseAlertEvent> {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public BaseAlertEvent deserialize(String topic, byte[] data) {
        try {
            JsonNode tree = mapper.readTree(data);
            String source = tree.get("source").asText();

            if ("BrazilModel".equalsIgnoreCase(source)) {
                return mapper.treeToValue(tree, BrazilAlertEvent.class);
            } else if ("TestModel".equalsIgnoreCase(source)) {
                return mapper.treeToValue(tree, TestAlertEvent.class);

            } else if ("AI-LSTM-SERVICE".equalsIgnoreCase(source)) {
                return mapper.treeToValue(tree, LstmAlertEvent.class);

            } else {
                throw new IllegalArgumentException("Unknown source type: " + source);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize alert event", e);
        }
    }

    @Override
    public void close() {
    }
}