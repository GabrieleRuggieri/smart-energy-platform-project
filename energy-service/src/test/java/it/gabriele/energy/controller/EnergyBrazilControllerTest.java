package it.gabriele.energy.controller;

import it.gabriele.energy.model.EnergyBrazilModel;
import it.gabriele.energy.service.EnergyBrazilService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnergyBrazilControllerTest {

    @InjectMocks
    EnergyBrazilController controller;

    @Mock
    EnergyBrazilService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStart() {
        Response response = controller.start();
        assertEquals(200, response.getStatus());
        assertEquals("Simulazione avviata", response.getEntity());
    }

    @Test
    void testGetAll() {
        List<EnergyBrazilModel> expected = List.of(new EnergyBrazilModel());
        when(service.getAllFromMemory()).thenReturn(expected);
        assertEquals(expected, controller.getAll());
    }

    @Test
    void testGetAverage() {
        when(service.getAverageConsumption()).thenReturn(123.45);
        assertEquals(123.45, controller.getAverage());
    }

    @Test
    void testGetMax() {
        EnergyBrazilModel max = new EnergyBrazilModel();
        when(service.getMaxRecord()).thenReturn(max);
        assertEquals(max, controller.getMax());
    }

    @Test
    void testGetDailyTotals() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        Map<LocalDate, Double> map = Map.of(date, 50.0);

        when(service.getDailyTotals()).thenReturn(map);

        Map<String, Double> result = controller.getDailyTotals();

        assertEquals(1, result.size());
        assertTrue(result.containsKey("2024-06-01"));
        assertEquals(50.0, result.get("2024-06-01"));
    }

    @Test
    void testGetBetween() {
        LocalDateTime from = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 6, 1, 23, 59);
        List<EnergyBrazilModel> list = List.of(new EnergyBrazilModel());

        when(service.filterByDateRange(from, to)).thenReturn(list);

        List<EnergyBrazilModel> result = controller.getBetween(from.toString(), to.toString());
        assertEquals(1, result.size());
    }

    @Test
    void testExportCsv() {
        when(service.exportCsv()).thenReturn("csv-data");
        Response response = controller.exportCsv();
        assertEquals(200, response.getStatus());
        assertEquals("csv-data", response.getEntity());
    }
}