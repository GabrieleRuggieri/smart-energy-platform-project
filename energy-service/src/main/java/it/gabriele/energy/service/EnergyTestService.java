package it.gabriele.energy.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import it.gabriele.alert.model.TestAlertEvent;
import it.gabriele.energy.model.EnergyTestModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnergyTestService {

    @Inject
    @Channel("test-alerts")
    Emitter<TestAlertEvent> alertEmitter;

    private static final String CSV_PATH = "/data/test_energy_data.csv";
    private final List<EnergyTestModel> realTimeData = new CopyOnWriteArrayList<>();

    private List<EnergyTestModel> csvData = new ArrayList<>();
    private int currentIndex = 0;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @PostConstruct
    void init() {
        csvData = loadDataFromCsv();
        Log.info("CSV test caricato in memoria");
    }

    void simulateSensorData() {
        if (currentIndex < csvData.size()) {
            EnergyTestModel newRecord = csvData.get(currentIndex++);
            realTimeData.add(newRecord);
            Log.infof("Nuovo record simulato dal CSV: %s", newRecord);

            if (newRecord.getAverageTemperature() > 20) {
                alertEmitter.send(new TestAlertEvent(
                        "TestModel",
                        LocalDateTime.now(),
                        "Temperatura anomala sopra 20°C",
                        newRecord.getBuildingType(),
                        newRecord.getAverageTemperature(),
                        newRecord.getEnergyConsumption()
                ));
            }

            if (newRecord.getEnergyConsumption() > 3500) {
                alertEmitter.send(new TestAlertEvent(
                        "TestModel",
                        LocalDateTime.now(),
                        "Consumo energetico elevato",
                        newRecord.getBuildingType(),
                        newRecord.getAverageTemperature(),
                        newRecord.getEnergyConsumption()
                ));
            }

        } else {
            Log.info("Fine del CSV test raggiunta. Simulazione terminata.");
            scheduler.shutdown();
        }
    }

    public void startScheduledSimulation() {
        if (isRunning.compareAndSet(false, true)) {
            Log.info("Simulazione avviata via endpoint");
            scheduler.scheduleAtFixedRate(this::simulateSensorData, 0, 30, TimeUnit.SECONDS);
        } else {
            Log.info("Simulazione già in esecuzione");
        }
    }

    // CSV reader
    public List<EnergyTestModel> loadDataFromCsv() {
        List<EnergyTestModel> records = new ArrayList<>();
        try (var reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH))))) {

            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                records.add(new EnergyTestModel(
                        line[0],
                        Integer.parseInt(line[1]),
                        Integer.parseInt(line[2]),
                        Integer.parseInt(line[3]),
                        Double.parseDouble(line[4]),
                        line[5],
                        Double.parseDouble(line[6])
                ));
            }

        } catch (Exception e) {
            Log.errorf("Errore durante la lettura del CSV: %s", e.getMessage());
        }

        return records;
    }

    // ========== METODI DOPPI ==========

    public List<EnergyTestModel> getAllFromMemory() {
        return new ArrayList<>(realTimeData);
    }

    public List<EnergyTestModel> getAllFromCsv() {
        return loadDataFromCsv();
    }

    public List<EnergyTestModel> filterByBuildingTypeFromMemory(String type) {
        return realTimeData.stream()
                .filter(r -> r.getBuildingType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> filterByBuildingTypeFromCsv(String type) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getBuildingType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public double getAverageConsumptionFromMemory() {
        return realTimeData.stream()
                .mapToDouble(EnergyTestModel::getEnergyConsumption)
                .average()
                .orElse(0);
    }

    public double getAverageConsumptionFromCsv() {
        return loadDataFromCsv().stream()
                .mapToDouble(EnergyTestModel::getEnergyConsumption)
                .average()
                .orElse(0);
    }

    public Map<String, Double> getAverageConsumptionPerDayFromMemory() {
        return realTimeData.stream()
                .collect(Collectors.groupingBy(
                        EnergyTestModel::getDayOfWeek,
                        Collectors.averagingDouble(EnergyTestModel::getEnergyConsumption)
                ));
    }

    public Map<String, Double> getAverageConsumptionPerDayFromCsv() {
        return loadDataFromCsv().stream()
                .collect(Collectors.groupingBy(
                        EnergyTestModel::getDayOfWeek,
                        Collectors.averagingDouble(EnergyTestModel::getEnergyConsumption)
                ));
    }

    public List<EnergyTestModel> filterBySurfaceRangeFromMemory(int min, int max) {
        return realTimeData.stream()
                .filter(r -> r.getSquareFootage() >= min && r.getSquareFootage() <= max)
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> filterBySurfaceRangeFromCsv(int min, int max) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getSquareFootage() >= min && r.getSquareFootage() <= max)
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> getTopByConsumptionFromMemory(int limit) {
        return realTimeData.stream()
                .sorted(Comparator.comparingDouble(EnergyTestModel::getEnergyConsumption).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> getTopByConsumptionFromCsv(int limit) {
        return loadDataFromCsv().stream()
                .sorted(Comparator.comparingDouble(EnergyTestModel::getEnergyConsumption).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> filterByTemperatureAboveFromMemory(double threshold) {
        return realTimeData.stream()
                .filter(r -> r.getAverageTemperature() > threshold)
                .collect(Collectors.toList());
    }

    public List<EnergyTestModel> filterByTemperatureAboveFromCsv(double threshold) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getAverageTemperature() > threshold)
                .collect(Collectors.toList());
    }

    public String getRawCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("buildingType,squareFootage,numberOfOccupants,appliancesUsed,averageTemperature,dayOfWeek,energyConsumption\n");
        for (EnergyTestModel r : realTimeData) {
            sb.append(String.format(
                    "%s,%d,%d,%d,%.2f,%s,%.2f\n",
                    r.getBuildingType(),
                    r.getSquareFootage(),
                    r.getNumberOfOccupants(),
                    r.getAppliancesUsed(),
                    r.getAverageTemperature(),
                    r.getDayOfWeek(),
                    r.getEnergyConsumption()
            ));
        }
        return sb.toString();
    }
}
