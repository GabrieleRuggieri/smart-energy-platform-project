package it.gabriele.energy.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnergyTestModelTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        EnergyTestModel model = new EnergyTestModel("residential", 120, 3, 5, 22.5, "Monday", 150.0);

        assertEquals("residential", model.getBuildingType());
        assertEquals(120, model.getSquareFootage());
        assertEquals(3, model.getNumberOfOccupants());
        assertEquals(5, model.getAppliancesUsed());
        assertEquals(22.5, model.getAverageTemperature());
        assertEquals("Monday", model.getDayOfWeek());
        assertEquals(150.0, model.getEnergyConsumption());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        EnergyTestModel model = new EnergyTestModel();

        model.setBuildingType("commercial");
        model.setSquareFootage(300);
        model.setNumberOfOccupants(10);
        model.setAppliancesUsed(8);
        model.setAverageTemperature(24.0);
        model.setDayOfWeek("Tuesday");
        model.setEnergyConsumption(300.5);

        assertEquals("commercial", model.getBuildingType());
        assertEquals(300, model.getSquareFootage());
        assertEquals(10, model.getNumberOfOccupants());
        assertEquals(8, model.getAppliancesUsed());
        assertEquals(24.0, model.getAverageTemperature());
        assertEquals("Tuesday", model.getDayOfWeek());
        assertEquals(300.5, model.getEnergyConsumption());
    }

    @Test
    void testEqualityAndToString() {
        EnergyTestModel a = new EnergyTestModel("x", 100, 2, 3, 21.0, "Friday", 180.0);
        EnergyTestModel b = new EnergyTestModel("x", 100, 2, 3, 21.0, "Friday", 180.0);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertTrue(a.toString().contains("buildingType=x"));
    }
}