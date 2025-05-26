package it.gabriele.energy.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import it.gabriele.energy.model.EnergyRecord;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnergyService {

    private static final String CSV_PATH = "/data/test_energy_data.csv";
    private final List<EnergyRecord> realTimeData = new CopyOnWriteArrayList<>();

    @PostConstruct
    void init() {
//        realTimeData.addAll(loadDataFromCsv());
        Log.info("Lista iniziale vuota");
    }

    @Scheduled(every = "30s")
    void simulateSensorData() {
        EnergyRecord newRecord = generateRandomRecord();
        realTimeData.add(newRecord);
        Log.infof("Nuovo record simulato ricevuto: %s", newRecord);
    }

    private EnergyRecord generateRandomRecord() {
        Random rand = new Random();
        String[] types = {"Residential", "Commercial", "Industrial"};
        String[] days = {"Weekday", "Weekend"};

        return new EnergyRecord(
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
    public List<EnergyRecord> loadDataFromCsv() {
        List<EnergyRecord> records = new ArrayList<>();
        try (var reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH))))) {

            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                records.add(new EnergyRecord(
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

    public List<EnergyRecord> getAllFromMemory() {
        return new ArrayList<>(realTimeData);
    }

    public List<EnergyRecord> getAllFromCsv() {
        return loadDataFromCsv();
    }

    public List<EnergyRecord> filterByBuildingTypeFromMemory(String type) {
        return realTimeData.stream()
                .filter(r -> r.getBuildingType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> filterByBuildingTypeFromCsv(String type) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getBuildingType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public double getAverageConsumptionFromMemory() {
        return realTimeData.stream()
                .mapToDouble(EnergyRecord::getEnergyConsumption)
                .average()
                .orElse(0);
    }

    public double getAverageConsumptionFromCsv() {
        return loadDataFromCsv().stream()
                .mapToDouble(EnergyRecord::getEnergyConsumption)
                .average()
                .orElse(0);
    }

    public Map<String, Double> getAverageConsumptionPerDayFromMemory() {
        return realTimeData.stream()
                .collect(Collectors.groupingBy(
                        EnergyRecord::getDayOfWeek,
                        Collectors.averagingDouble(EnergyRecord::getEnergyConsumption)
                ));
    }

    public Map<String, Double> getAverageConsumptionPerDayFromCsv() {
        return loadDataFromCsv().stream()
                .collect(Collectors.groupingBy(
                        EnergyRecord::getDayOfWeek,
                        Collectors.averagingDouble(EnergyRecord::getEnergyConsumption)
                ));
    }

    public List<EnergyRecord> filterBySurfaceRangeFromMemory(int min, int max) {
        return realTimeData.stream()
                .filter(r -> r.getSquareFootage() >= min && r.getSquareFootage() <= max)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> filterBySurfaceRangeFromCsv(int min, int max) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getSquareFootage() >= min && r.getSquareFootage() <= max)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> getTopByConsumptionFromMemory(int limit) {
        return realTimeData.stream()
                .sorted(Comparator.comparingDouble(EnergyRecord::getEnergyConsumption).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> getTopByConsumptionFromCsv(int limit) {
        return loadDataFromCsv().stream()
                .sorted(Comparator.comparingDouble(EnergyRecord::getEnergyConsumption).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> filterByTemperatureAboveFromMemory(double threshold) {
        return realTimeData.stream()
                .filter(r -> r.getAverageTemperature() > threshold)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> filterByTemperatureAboveFromCsv(double threshold) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getAverageTemperature() > threshold)
                .collect(Collectors.toList());
    }

    public String getRawCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("buildingType,squareFootage,numberOfOccupants,appliancesUsed,averageTemperature,dayOfWeek,energyConsumption\n");
        for (EnergyRecord r : realTimeData) {
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
