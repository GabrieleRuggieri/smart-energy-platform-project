package it.gabriele.alert.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseAlertEventTest {

    @Test
    void testSettersAndGettersViaSubclass() {
        // Classe fittizia per testare BaseAlertEvent
        class DummyAlert extends BaseAlertEvent {}

        DummyAlert alert = new DummyAlert();
        LocalDateTime now = LocalDateTime.now();

        alert.setSource("DummySource");
        alert.setTimestamp(now);
        alert.setMessage("DummyMessage");

        assertEquals("DummySource", alert.getSource());
        assertEquals(now, alert.getTimestamp());
        assertEquals("DummyMessage", alert.getMessage());
    }
}