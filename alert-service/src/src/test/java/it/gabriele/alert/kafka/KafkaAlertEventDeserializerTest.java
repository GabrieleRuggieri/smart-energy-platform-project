package it.gabriele.alert.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gabriele.alert.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class KafkaAlertEventDeserializerTest {

    KafkaAlertEventDeserializer deserializer;
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        deserializer = new KafkaAlertEventDeserializer();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testDeserializeBrazilAlertEvent() throws Exception {
        BrazilAlertEvent event = new BrazilAlertEvent(
                "BrazilModel",
                LocalDateTime.of(2024, 6, 1, 12, 0),
                "Allarme consumo",
                20000.0
        );

        byte[] json = mapper.writeValueAsBytes(event);
        BaseAlertEvent result = deserializer.deserialize("alert-topic", json);

        assertTrue(result instanceof BrazilAlertEvent);
        assertEquals("BrazilModel", result.getSource());
    }

    @Test
    void testDeserializeTestAlertEvent() throws Exception {
        TestAlertEvent event = new TestAlertEvent(
                "TestModel",
                LocalDateTime.of(2024, 6, 1, 15, 0),
                "Allarme temperatura",
                "residential",
                25.5,
                3000.0
        );

        byte[] json = mapper.writeValueAsBytes(event);
        BaseAlertEvent result = deserializer.deserialize("alert-topic", json);

        assertTrue(result instanceof TestAlertEvent);
        assertEquals("TestModel", result.getSource());
    }

    @Test
    void testDeserializeLstmAlertEvent() throws Exception {
        LstmAlertEvent event = new LstmAlertEvent(
                "AI-LSTM-SERVICE",
                LocalDateTime.of(2024, 6, 1, 18, 0),
                "Previsione critica",
                1234.56
        );

        byte[] json = mapper.writeValueAsBytes(event);
        BaseAlertEvent result = deserializer.deserialize("alert-topic", json);

        assertTrue(result instanceof LstmAlertEvent);
        assertEquals("AI-LSTM-SERVICE", result.getSource());
    }

    @Test
    void testDeserializeUnknownSourceThrows() {
        String json = """
                {
                    "source": "UnknownSource",
                    "timestamp": "2024-06-01T12:00:00",
                    "message": "Messaggio di test"
                }
                """;

        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        Exception exception = assertThrows(RuntimeException.class, () ->
                deserializer.deserialize("alert-topic", data)
        );

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}