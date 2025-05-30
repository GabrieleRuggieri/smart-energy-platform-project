package it.gabriele.energy.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import it.gabriele.alert.model.BrazilAlertEvent;
import it.gabriele.energy.model.EnergyBrazilModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@ApplicationScoped
public class EnergyBrazilService {

    @Inject
    @Channel("brazil-alerts")
    Emitter<BrazilAlertEvent> alertEmitter;

    private static final String CSV_PATH = "/data/energy_demand_hourly_brazil.csv";
    private final List<EnergyBrazilModel> csvData = new ArrayList<>();
    private final List<EnergyBrazilModel> realTimeData = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private int currentIndex = 0;

    @PostConstruct
    void init() {
        csvData.addAll(loadDataFromCsv());
        Log.info("Lista iniziale con file csv caricata.");
    }

    public void simulateHourlyDemand() {
        if (currentIndex < csvData.size()) {
            EnergyBrazilModel newRecord = csvData.get(currentIndex++);
            realTimeData.add(newRecord);
            Log.infof("Nuovo record simulato: %s", newRecord);

            if (newRecord.getHourlyEnergyConsumption() > 20000) {
                alertEmitter.send(new BrazilAlertEvent(
                        "BrazilModel",
                        newRecord.getDate(),
                        "Consumo critico sopra 20000",
                        newRecord.getHourlyEnergyConsumption()
                ));
            }
        } else {
            Log.info("Fine del CSV raggiunta. Simulazione terminata.");
            scheduler.shutdown();
        }
    }

    public void startSimulation() {
        if (isRunning.compareAndSet(false, true)) {
            Log.info("Simulazione hourly demand avviata via endpoint");
            scheduler.scheduleAtFixedRate(this::simulateHourlyDemand, 0, 30, TimeUnit.SECONDS);
        } else {
            Log.info("Simulazione già in esecuzione");
        }
    }

    public List<EnergyBrazilModel> loadDataFromCsv() {
        List<EnergyBrazilModel> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (var reader = new CSVReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH))))) {

            reader.readNext(); // Skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                records.add(new EnergyBrazilModel(
                        LocalDateTime.parse(line[0], formatter),
                        Double.parseDouble(line[1])
                ));
            }

        } catch (Exception e) {
            Log.errorf("Errore durante la lettura del CSV: %s", e.getMessage());
        }

        return records;
    }

    public List<EnergyBrazilModel> getAllFromMemory() {
        return new ArrayList<>(realTimeData);
    }

    public double getAverageConsumption() {
        return realTimeData.stream()
                .mapToDouble(EnergyBrazilModel::getHourlyEnergyConsumption)
                .average()
                .orElse(0.0);
    }

    public EnergyBrazilModel getMaxRecord() {
        return realTimeData.stream()
                .max(Comparator.comparingDouble(EnergyBrazilModel::getHourlyEnergyConsumption))
                .orElse(null);
    }

    public Map<LocalDate, Double> getDailyTotals() {
        return realTimeData.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDate().toLocalDate(),
                        Collectors.summingDouble(EnergyBrazilModel::getHourlyEnergyConsumption)
                ));
    }

    public List<EnergyBrazilModel> filterByDateRange(LocalDateTime from, LocalDateTime to) {
        return realTimeData.stream()
                .filter(r -> !r.getDate().isBefore(from) && !r.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    public String exportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("date,hourly_demand\n");
        for (EnergyBrazilModel r : realTimeData) {
            sb.append(String.format("%s,%.2f\n", r.getDate(), r.getHourlyEnergyConsumption()));
        }
        return sb.toString();
    }
}
