package it.gabriele.energy.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import it.gabriele.energy.model.EnergyRecord;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnergyService {

    private static final String CSV_PATH = "/data/test-energy-data.csv";

    public List<EnergyRecord> loadDataFromCsv() {
        List<EnergyRecord> records = new ArrayList<>();
        Log.info("Inizio caricamento dati dal CSV...");

        try (var reader = new CSVReader(new InputStreamReader(
                getClass().getResourceAsStream(CSV_PATH)))) {

            String[] line;
            reader.readNext(); // skip header
            int row = 0;
            while ((line = reader.readNext()) != null) {
                EnergyRecord record = new EnergyRecord(
                        line[0],
                        Integer.parseInt(line[1]),
                        Integer.parseInt(line[2]),
                        Integer.parseInt(line[3]),
                        Double.parseDouble(line[4]),
                        line[5],
                        Double.parseDouble(line[6])
                );
                records.add(record);
                row++;
            }

            Log.infof("Caricamento completato. Righe lette: %s", row);

        } catch (Exception e) {
            Log.errorf("Errore durante la lettura del CSV: %s", e.getMessage());
            e.printStackTrace();
        }

        return records;
    }

    public List<EnergyRecord> filterByBuildingType(String type) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getBuildingType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public double getAverageConsumption() {
        return loadDataFromCsv().stream()
                .mapToDouble(EnergyRecord::getEnergyConsumption)
                .average()
                .orElse(0);
    }

    public Map<String, Double> getAverageConsumptionPerDay() {
        return loadDataFromCsv().stream()
                .collect(Collectors.groupingBy(
                        EnergyRecord::getDayOfWeek,
                        Collectors.averagingDouble(EnergyRecord::getEnergyConsumption)
                ));
    }

    public List<EnergyRecord> filterBySurfaceRange(int min, int max) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getSquareFootage() >= min && r.getSquareFootage() <= max)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> getTopByConsumption(int limit) {
        return loadDataFromCsv().stream()
                .sorted(Comparator.comparingDouble(EnergyRecord::getEnergyConsumption).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<EnergyRecord> filterByTemperatureAbove(double threshold) {
        return loadDataFromCsv().stream()
                .filter(r -> r.getAverageTemperature() > threshold)
                .collect(Collectors.toList());
    }

    public String getRawCsv() {
        try (Scanner scanner = new Scanner(getClass().getResourceAsStream(CSV_PATH))) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            Log.error("Errore durante il caricamento CSV raw");
            return "";
        }
    }
}
