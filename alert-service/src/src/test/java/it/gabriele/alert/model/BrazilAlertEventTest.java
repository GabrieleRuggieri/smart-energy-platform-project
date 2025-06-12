package it.gabriele.alert.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BrazilAlertEventTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime timestamp = LocalDateTime.now();
        BrazilAlertEvent event = new BrazilAlertEvent("BrazilModel", timestamp, "Overload", 12345.67);

        assertEquals("BrazilModel", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Overload", event.getMessage());
        assertEquals(12345.67, event.getHourlyEnergyConsumption());
    }

    @Test
    void testSetters() {
        BrazilAlertEvent event = new BrazilAlertEvent();
        LocalDateTime timestamp = LocalDateTime.now();

        event.setSource("BrazilModel");
        event.setTimestamp(timestamp);
        event.setMessage("Test");
        event.setHourlyEnergyConsumption(9999.0);

        assertEquals("BrazilModel", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Test", event.getMessage());
        assertEquals(9999.0, event.getHourlyEnergyConsumption());
    }
}