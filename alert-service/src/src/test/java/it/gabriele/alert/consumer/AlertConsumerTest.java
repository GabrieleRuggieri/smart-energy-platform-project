package it.gabriele.alert.consumer;

import it.gabriele.alert.model.BrazilAlertEvent;
import it.gabriele.alert.model.TestAlertEvent;
import it.gabriele.alert.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class AlertConsumerTest {

    @InjectMocks
    AlertConsumer alertConsumer;

    @Mock
    AlertService alertService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        alertConsumer.alertService = alertService;
    }

    @Test
    void testReceiveWithBrazilAlertEvent() {
        BrazilAlertEvent alert = new BrazilAlertEvent(
                "brazil-source",
                LocalDateTime.now(),
                "Allarme consumo critico",
                21000.0
        );

        alertConsumer.receive(alert);

        verify(alertService, times(1)).handle(alert);
    }

    @Test
    void testReceiveWithTestAlertEvent() {
        TestAlertEvent alert = new TestAlertEvent(
                "test-source",
                LocalDateTime.now(),
                "Allarme temperatura",
                "residential",
                27.5,
                3800.0
        );

        alertConsumer.receive(alert);

        verify(alertService, times(1)).handle(alert);
    }
}