package it.gabriele.energy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyBrazilModel {

    private LocalDateTime index;
    private double hourlyDemand;

}
