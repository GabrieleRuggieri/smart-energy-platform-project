package it.gabriele.alert.service;

import it.gabriele.alert.model.BrazilAlertEvent;
import it.gabriele.alert.model.LstmAlertEvent;
import it.gabriele.alert.model.TestAlertEvent;
import it.gabriele.alert.model.BaseAlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class AlertServiceTest {

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService();
    }

    @Test
    void testHandleBrazilAlertEvent() {
        BrazilAlertEvent event = new BrazilAlertEvent(
                "BrazilModel",
                LocalDateTime.now(),
                "Consumo alto",
                21000.0
        );
        alertService.handle(event);
        // Il log è interno, ma il metodo viene coperto
    }

    @Test
    void testHandleTestAlertEvent() {
        TestAlertEvent event = new TestAlertEvent(
                "TestModel",
                LocalDateTime.now(),
                "Temperatura critica",
                "Commerciale",
                24.5,
                3700.0
        );
        alertService.handle(event);
    }

    @Test
    void testHandleLstmAlertEvent() {
        LstmAlertEvent event = new LstmAlertEvent(
                "AI-LSTM-SERVICE",
                LocalDateTime.now(),
                "Picco previsto",
                5000.0
        );
        alertService.handle(event);
    }

    @Test
    void testHandleUnknownAlertEvent() {
        class UnknownAlertEvent extends BaseAlertEvent {
            public UnknownAlertEvent() {
                super("Unknown", LocalDateTime.now(), "Evento ignoto");
            }
        }

        BaseAlertEvent unknownEvent = new UnknownAlertEvent();
        alertService.handle(unknownEvent);
    }
}