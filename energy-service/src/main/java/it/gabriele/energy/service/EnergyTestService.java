package it.gabriele.energy.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import it.gabriele.energy.model.EnergyTestModel;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnergyTestService {

    private static final String CSV_PATH = "/data/test_energy_data.csv";
    private final List<EnergyTestModel> realTimeData = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

//    @PostConstruct
//    void init() {
//        realTimeData.addAll(loadDataFromCsv());
//        Log.info("Lista iniziale con file csv");
//    }

//    @Scheduled(every = "30s")
    void simulateSensorData() {
        EnergyTestModel newRecord = generateRandomRecord();
        realTimeData.add(newRecord);
        Log.infof("Nuovo record simulato ricevuto: %s", newRecord);
    }

    public void startScheduledSimulation() {
        if (isRunning.compareAndSet(false, true)) {
            Log.info("Simulazione avviata via endpoint");
            scheduler.scheduleAtFixedRate(this::simulateSensorData, 0, 30, TimeUnit.SECONDS);
        } else {
            Log.info("Simulazione già in esecuzione");
        }
    }

    private EnergyTestModel generateRandomRecord() {
        Random rand = new Random();
        String[] types = {"Residential", "Commercial", "Industrial"};
        String[] days = {"Weekday", "Weekend"};

        return new EnergyTestModel(
                types[rand.nextInt(types.length)],
                1000 + rand.nextInt(50000),
                1 + rand.nextInt(100),
                1 + rand.nextInt(50),
                10.0 + rand.nextDouble() * 25,
                days[rand.nextInt(days.length)],
                1000 + rand.nextDouble() * 5000
        );
    }

    // 📂 CSV reader
    public List<EnergyTestModel> loadDataFromCsv() {
        List<EnergyTestModel> records = new ArrayList<>();
        try (var reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH))))) {

            reader.readNext(); // skip header
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
