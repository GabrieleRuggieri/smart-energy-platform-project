package it.gabriele.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrazilAlertEvent extends BaseAlertEvent {
    public Double hourlyEnergyConsumption;

    public BrazilAlertEvent(String source, LocalDateTime timestamp, String message, Double hourlyEnergyConsumption) {
        super(source, timestamp, message);
        this.hourlyEnergyConsumption = hourlyEnergyConsumption;
    }

}
