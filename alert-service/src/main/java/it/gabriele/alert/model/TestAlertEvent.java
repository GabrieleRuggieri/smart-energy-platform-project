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
public class TestAlertEvent extends BaseAlertEvent {
    public String buildingType;
    public Double temperature;
    public Double energyConsumption;

    public TestAlertEvent(String source, LocalDateTime timestamp, String message, String buildingType, Double temperature, Double energyConsumption) {
        super(source, timestamp, message);
        this.buildingType = buildingType;
        this.temperature = temperature;
        this.energyConsumption = energyConsumption;
    }
}
