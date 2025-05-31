package it.gabriele.ai.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class AlertProducer {

    @Channel("ai-alerts")
    Emitter<String> alertEmitter;

    public void sendAlert(String message) {
        alertEmitter.send(message);
    }

}
