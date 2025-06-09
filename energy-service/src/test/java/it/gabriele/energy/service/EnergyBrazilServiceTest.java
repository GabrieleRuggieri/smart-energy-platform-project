package it.gabriele.energy.service;

import it.gabriele.energy.model.EnergyBrazilModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import it.gabriele.alert.model.BrazilAlertEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnergyBrazilServiceTest {

    @InjectMocks
    EnergyBrazilService service;

    @Mock
    Emitter<BrazilAlertEvent> emitter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service.alertEmitter = emitter;

        // Simula dati real-time
        service.getAllFromMemory().clear();
        service.getAllFromMemory().addAll(List.of(
                new EnergyBrazilModel(LocalDateTime.of(2024, 6, 1, 10, 0), 10000),
                new EnergyBrazilModel(LocalDateTime.of(2024, 6, 1, 11, 0), 30000)
        ));
    }

    @Test
    void testGetAverageConsumption() {
        double avg = service.getAverageConsumption();
        assertEquals(20000.0, avg);
    }

    @Test
    void testGetMaxRecord() {
        EnergyBrazilModel max = service.getMaxRecord();
        assertEquals(30000.0, max.getHourlyEnergyConsumption());
    }

    @Test
    void testGetDailyTotals() {
        Map<LocalDate, Double> totals = service.getDailyTotals();
        assertEquals(1, totals.size());
        assertTrue(totals.containsKey(LocalDate.of(2024, 6, 1)));
        assertEquals(40000.0, totals.get(LocalDate.of(2024, 6, 1)));
    }

    @Test
    void testFilterByDateRange() {
        List<EnergyBrazilModel> result = service.filterByDateRange(
                LocalDateTime.of(2024, 6, 1, 9, 0),
                LocalDateTime.of(2024, 6, 1, 10, 0)
        );
        assertEquals(1, result.size());
    }

    @Test
    void testExportCsv() {
        String csv = service.exportCsv();
        assertTrue(csv.contains("date,hourly_demand"));
        assertTrue(csv.contains("2024-06-01"));
    }
}