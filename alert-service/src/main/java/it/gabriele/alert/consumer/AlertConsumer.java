package it.gabriele.alert.consumer;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import it.gabriele.alert.model.BaseAlertEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import it.gabriele.alert.service.AlertService;

@ApplicationScoped
public class AlertConsumer {


    @Inject
    AlertService alertService;

    @Incoming("alerts")
    @Blocking
    public void receive(BaseAlertEvent alert) {
        Log.info("🟢 ALERT RICEVUTO da " + alert.getSource() + ": " + alert.getMessage());
        alertService.handle(alert);
    }
}