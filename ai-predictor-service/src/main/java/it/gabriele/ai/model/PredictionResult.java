package it.gabriele.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResult {

    private LocalDateTime date;
    private double predictedConsumption;
    private boolean alertTriggered;

}
