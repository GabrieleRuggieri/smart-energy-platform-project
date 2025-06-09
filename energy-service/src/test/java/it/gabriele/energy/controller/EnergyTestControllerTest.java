package it.gabriele.energy.controller;

import it.gabriele.energy.model.EnergyTestModel;
import it.gabriele.energy.service.EnergyTestService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnergyTestControllerTest {

    @InjectMocks
    EnergyTestController controller;

    @Mock
    EnergyTestService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartSimulation() {
        Response response = controller.startSimulation();
        assertEquals(200, response.getStatus());
        assertEquals("Simulazione sensori avviata", response.getEntity());
    }

    @Test
    void testGetAllRecords() {
        List<EnergyTestModel> expected = List.of(new EnergyTestModel());
        when(service.getAllFromMemory()).thenReturn(expected);
        assertEquals(expected, controller.getAllRecords());
    }

    @Test
    void testFilterByBuildingType() {
        List<EnergyTestModel> expected = List.of(new EnergyTestModel());
        when(service.filterByBuildingTypeFromMemory("residential")).thenReturn(expected);
        assertEquals(expected, controller.filterByBuildingType("residential"));
    }

    @Test
    void testAverageConsumption() {
        when(service.getAverageConsumptionFromMemory()).thenReturn(42.0);
        assertEquals(42.0, controller.averageConsumption());
    }

    @Test
    void testAveragePerDay() {
        Map<String, Double> map = Map.of("2024-06-01", 88.0);
        when(service.getAverageConsumptionPerDayFromMemory()).thenReturn(map);
        assertEquals(map, controller.averagePerDay());
    }

    @Test
    void testSurfaceRange() {
        List<EnergyTestModel> expected = List.of(new EnergyTestModel());
        when(service.filterBySurfaceRangeFromMemory(50, 100)).thenReturn(expected);
        assertEquals(expected, controller.surfaceRange(50, 100));
    }

    @Test
    void testTop() {
        List<EnergyTestModel> expected = List.of(new EnergyTestModel(), new EnergyTestModel());
        when(service.getTopByConsumptionFromMemory(2)).thenReturn(expected);
        assertEquals(expected, controller.top(2));
    }

    @Test
    void testTemperatureAbove() {
        List<EnergyTestModel> expected = List.of(new EnergyTestModel());
        when(service.filterByTemperatureAboveFromMemory(30.0)).thenReturn(expected);
        assertEquals(expected, controller.temperatureAbove(30.0));
    }

    @Test
    void testExportCsv() {
        when(service.getRawCsv()).thenReturn("csv-content");
        Response response = controller.exportCsv();
        assertEquals(200, response.getStatus());
        assertEquals("csv-content", response.getEntity());
    }
}