package it.gabriele.alert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LstmAlertEvent extends BaseAlertEvent {

    private double hourlyEnergyConsumption;

}
