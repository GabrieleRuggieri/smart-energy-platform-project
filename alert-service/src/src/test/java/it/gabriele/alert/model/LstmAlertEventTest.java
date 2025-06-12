package it.gabriele.alert.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LstmAlertEventTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime timestamp = LocalDateTime.now();
        LstmAlertEvent event = new LstmAlertEvent("AI-LSTM-SERVICE", timestamp, "Prediction alert", 5000.0);

        assertEquals("AI-LSTM-SERVICE", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Prediction alert", event.getMessage());
        assertEquals(5000.0, event.getHourlyEnergyConsumption());
    }

    @Test
    void testSetters() {
        LstmAlertEvent event = new LstmAlertEvent();
        LocalDateTime timestamp = LocalDateTime.now();

        event.setSource("AI-LSTM-SERVICE");
        event.setTimestamp(timestamp);
        event.setMessage("Predicted spike");
        event.setHourlyEnergyConsumption(6000.0);

        assertEquals("AI-LSTM-SERVICE", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Predicted spike", event.getMessage());
        assertEquals(6000.0, event.getHourlyEnergyConsumption());
    }
}