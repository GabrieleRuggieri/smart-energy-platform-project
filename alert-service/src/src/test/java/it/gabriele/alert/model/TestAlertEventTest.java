package it.gabriele.alert.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TestAlertEventTest {

    @Test
    void testConstructorAndGetters() {
        LocalDateTime timestamp = LocalDateTime.now();
        TestAlertEvent event = new TestAlertEvent("TestModel", timestamp, "Temperature alert", "industrial", 26.5, 3200.0);

        assertEquals("TestModel", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Temperature alert", event.getMessage());
        assertEquals("industrial", event.getBuildingType());
        assertEquals(26.5, event.getTemperature());
        assertEquals(3200.0, event.getEnergyConsumption());
    }

    @Test
    void testSetters() {
        TestAlertEvent event = new TestAlertEvent();
        LocalDateTime timestamp = LocalDateTime.now();

        event.setSource("TestModel");
        event.setTimestamp(timestamp);
        event.setMessage("Alert msg");
        event.setBuildingType("residential");
        event.setTemperature(27.0);
        event.setEnergyConsumption(3100.0);

        assertEquals("TestModel", event.getSource());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Alert msg", event.getMessage());
        assertEquals("residential", event.getBuildingType());
        assertEquals(27.0, event.getTemperature());
        assertEquals(3100.0, event.getEnergyConsumption());
    }
}