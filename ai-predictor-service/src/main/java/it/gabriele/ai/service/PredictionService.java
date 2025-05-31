package it.gabriele.ai.service;

import com.opencsv.CSVReader;
import io.quarkus.logging.Log;
import it.gabriele.ai.kafka.AlertProducer;
import it.gabriele.ai.model.PredictionResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import smile.data.DataFrame;
import smile.data.formula.Formula;
import smile.data.vector.DoubleVector;
import smile.regression.LinearModel;
import smile.regression.OLS;

import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class PredictionService {

    @Inject
    AlertProducer alertProducer;

    private LinearModel model;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String CSV_PATH = "/data/energy_demand_hourly_brazil.csv";
    private static final double ALERT_THRESHOLD = 20000.0;


    @PostConstruct
    public void init() {
        try {
            Log.info("Inizio caricamento e training del modello AI...");

            List<LocalDateTime> dates = new ArrayList<>();
            List<Double> consumptions = new ArrayList<>();

            loadCsvData(dates, consumptions);

            double[] timestamps = dates.stream()
                    .mapToDouble(dt -> dt.atZone(java.time.ZoneId.systemDefault()).toEpochSecond())
                    .toArray();

            double[] demand = consumptions.stream()
                    .mapToDouble(Double::doubleValue)
                    .toArray();

            DataFrame training = DataFrame.of(
                    DoubleVector.of("timestamp", timestamps),
                    DoubleVector.of("hourly_demand", demand)
            );

            model = OLS.fit(Formula.lhs("hourly_demand"), training);
            Log.infof("Modello addestrato con successo su %d record.", demand.length);

        } catch (Exception e) {
            Log.error("Errore durante il training del modello AI", e);
        }
    }

    private void loadCsvData(List<LocalDateTime> dates, List<Double> consumptions) {

        try (CSVReader reader = new CSVReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(CSV_PATH))))) {

            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                dates.add(LocalDateTime.parse(line[0], formatter));
                consumptions.add(Double.parseDouble(line[1]));
            }

            Log.infof("CSV caricato: %d righe lette.", dates.size());
        } catch (Exception e) {
            Log.errorf("Errore durante la lettura del CSV: %s", e.getMessage());
        }
    }

    public PredictionResult predict(LocalDateTime dateTime) {
        double timestamp = dateTime.atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
        double predicted = model.predict(new double[]{timestamp});

        boolean alert = predicted > ALERT_THRESHOLD;

        if (alert) {
            String message = String.format(
                    "AI Forecast Alert: consumo previsto il %s è %.2f (soglia: %.2f)",
                    dateTime, predicted, ALERT_THRESHOLD
            );
            alertProducer.sendAlert(message);
            Log.warn(message);
        } else {
            Log.infof("Previsione %s: %.2f (nessun alert)", dateTime, predicted);
        }

        return new PredictionResult(dateTime, predicted, alert);
    }

    public List<PredictionResult> predictBatch(List<LocalDateTime> dates) {
        List<PredictionResult> results = new ArrayList<>();
        for (LocalDateTime dt : dates) {
            results.add(predict(dt));
        }
        return results;
    }
}