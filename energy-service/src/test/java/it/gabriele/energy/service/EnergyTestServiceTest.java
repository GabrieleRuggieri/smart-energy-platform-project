package it.gabriele.energy.service;

import it.gabriele.energy.model.EnergyTestModel;
import it.gabriele.alert.model.TestAlertEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EnergyTestServiceTest {

    @InjectMocks
    EnergyTestService service;

    @Mock
    Emitter<TestAlertEvent> emitter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service.alertEmitter = emitter;

        // Simula dati real-time
        service.getAllFromMemory().clear();
        service.getAllFromMemory().addAll(List.of(
                new EnergyTestModel("residential", 100, 2, 3, 21.5, "Monday", 1500.0),
                new EnergyTestModel("commercial", 300, 6, 7, 25.0, "Monday", 4000.0)
        ));
    }

    @Test
    void testGetAverageConsumptionFromMemory() {
        double avg = service.getAverageConsumptionFromMemory();
        assertEquals(2750.0, avg);
    }

    @Test
    void testGetAverageConsumptionPerDayFromMemory() {
        Map<String, Double> result = service.getAverageConsumptionPerDayFromMemory();
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Monday"));
        assertEquals(2750.0, result.get("Monday"));
    }

    @Test
    void testFilterByBuildingTypeFromMemory() {
        List<EnergyTestModel> filtered = service.filterByBuildingTypeFromMemory("commercial");
        assertEquals(1, filtered.size());
        assertEquals("commercial", filtered.get(0).getBuildingType());
    }

    @Test
    void testFilterBySurfaceRangeFromMemory() {
        List<EnergyTestModel> filtered = service.filterBySurfaceRangeFromMemory(90, 120);
        assertEquals(1, filtered.size());
    }

    @Test
    void testGetTopByConsumptionFromMemory() {
        List<EnergyTestModel> top = service.getTopByConsumptionFromMemory(1);
        assertEquals(1, top.size());
        assertEquals(4000.0, top.get(0).getEnergyConsumption());
    }

    @Test
    void testFilterByTemperatureAboveFromMemory() {
        List<EnergyTestModel> result = service.filterByTemperatureAboveFromMemory(22.0);
        assertEquals(1, result.size());
        assertEquals("commercial", result.get(0).getBuildingType());
    }

    @Test
    void testExportCsv() {
        String csv = service.getRawCsv();
        assertTrue(csv.contains("buildingType,squareFootage"));
        assertTrue(csv.contains("residential"));
    }
}