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
public class LstmAlertEvent extends BaseAlertEvent {

    private double hourlyEnergyConsumption;

    public LstmAlertEvent(String source, LocalDateTime timestamp, String message, double hourlyEnergyConsumption) {
        super(source, timestamp, message);
        this.hourlyEnergyConsumption = hourlyEnergyConsumption;
    }

}
