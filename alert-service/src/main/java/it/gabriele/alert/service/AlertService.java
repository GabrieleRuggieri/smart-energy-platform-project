package it.gabriele.alert.service;

import io.quarkus.logging.Log;
import it.gabriele.alert.model.BaseAlertEvent;
import it.gabriele.alert.model.BrazilAlertEvent;
import it.gabriele.alert.model.LstmAlertEvent;
import it.gabriele.alert.model.TestAlertEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlertService {

    public void handle(BaseAlertEvent event) {
        if (event instanceof BrazilAlertEvent b) {
            Log.infof("Handled Brazil alert: consumption = {%s}", b.getHourlyEnergyConsumption());
        } else if (event instanceof TestAlertEvent t) {
            Log.infof("Handled Test alert: temp = {%s}, building = {%s}", t.getTemperature(), t.getBuildingType());
        } else if (event instanceof LstmAlertEvent lstm) {
            Log.infof("Handled Lstm Alert: hourly consumption = {%s}", lstm.getHourlyEnergyConsumption());
        } else {
            Log.warnf("Unhandled alert type: {}", event.getClass().getSimpleName());
        }
    }
}