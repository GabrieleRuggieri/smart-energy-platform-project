package it.gabriele.energy.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EnergyBrazilModelTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        LocalDateTime date = LocalDateTime.of(2024, 6, 1, 12, 0);
        double consumption = 12345.67;

        EnergyBrazilModel model = new EnergyBrazilModel(date, consumption);

        assertEquals(date, model.getDate());
        assertEquals(consumption, model.getHourlyEnergyConsumption());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        EnergyBrazilModel model = new EnergyBrazilModel();

        LocalDateTime date = LocalDateTime.of(2024, 6, 1, 15, 30);
        double consumption = 9999.99;

        model.setDate(date);
        model.setHourlyEnergyConsumption(consumption);

        assertEquals(date, model.getDate());
        assertEquals(consumption, model.getHourlyEnergyConsumption());
    }

    @Test
    void testToStringAndEquality() {
        LocalDateTime date = LocalDateTime.now();
        double consumption = 200.0;

        EnergyBrazilModel a = new EnergyBrazilModel(date, consumption);
        EnergyBrazilModel b = new EnergyBrazilModel(date, consumption);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.toString().contains("hourlyEnergyConsumption=" + consumption));
    }
}