package it.gabriele.energy.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergyTestModel {

    private String buildingType;
    private int squareFootage;
    private int numberOfOccupants;
    private int appliancesUsed;
    private double averageTemperature;
    private String dayOfWeek;
    private double energyConsumption;

}