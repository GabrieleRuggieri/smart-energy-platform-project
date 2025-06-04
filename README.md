# ⚡ Smart Energy Platform

La **Smart Energy Platform** è una soluzione distribuita modulare per il monitoraggio, l’analisi e la predizione dei consumi energetici. Utilizza una combinazione di microservizi, tecnologie di streaming, machine learning e strumenti di osservabilità per fornire insight real-time e forecast affidabili, scalabili e integrabili.

---

## 🧩 Architettura

La piattaforma è composta da diversi microservizi containerizzati, ciascuno con un ruolo specifico, orchestrati tramite Docker Compose:

```plaintext
+-------------------+        +------------------+       +-----------------+
|   energy-service  +-------> Kafka Topics <----+ alert-service         |
+-------------------+        +------------------+       +-----------------+
         |                                                        |
         v                                                        v
 Grafana/Kibana                                           ai-lstm-service
(Visualizzazione)                                          (Predizione ML)
```

---

## 🧱 Microservizi

| Servizio              | Linguaggio | Ruolo |
|-----------------------|------------|-------|
| `energy-service`      | Java (Quarkus) | Espone API per interrogare i dati energetici (CSV) |
| `alert-service`       | Java (Quarkus) | Genera e consuma eventi di allerta da Kafka |
| `ai-lstm-service`     | Python (FastAPI) | Predice i consumi futuri con modelli LSTM addestrati |
| `ai-predictor-service`| Java (Quarkus) | Versione legacy del servizio predittivo (da dismettere) |
| `elasticsearch`       | - | Archiviazione dei log e dati per Kibana |
| `kibana`              | - | Dashboard e analisi log |
| `grafana`             | - | Dashboard metrica dei consumi |
| `filebeat`            | - | Forwarding dei log verso Elasticsearch |
| `kafka`               | - | Broker eventi per allerta, predizioni, consumi |
| `sonarqube`           | - | Analisi statica del codice, test coverage |

---

## 🚀 Avvio rapido

Assicurati di avere installato:

- Docker
- Docker Compose
- Java 17+
- Maven

### ▶️ Esecuzione

```bash
docker-compose up --build
```

### 🔍 Servizi accessibili

| Servizio     | URL                       | Note                |
|--------------|---------------------------|---------------------|
| Grafana      | http://localhost:3000     | admin / admin       |
| Kibana       | http://localhost:5601     |                     |
| SonarQube    | http://localhost:9000     | token personale     |
| Energy API   | http://localhost:8080     | Documentazione API  |
| Alert API    | http://localhost:8081     | Kafka integration   |
| LSTM API     | http://localhost:8000     | `/predict` endpoint |

---

## ✅ Testing & Quality

### ✔️ Test Unitari

I test sono implementati nei microservizi Java con JUnit 5 e Mockito.

Per eseguirli:

```bash
cd <servizio>
./mvnw test
```

### 📊 Coverage & Analisi Codice

Assicurati che SonarQube sia in esecuzione (`localhost:9000`) poi:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.projectKey=<nome-progetto> \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<your-token>
```

Puoi creare un token da **My Account > Security** in SonarQube.

---

## 📦 Dataset Energetico

Il dataset utilizzato per l’addestramento e testing del modello LSTM è basato su dati reali del Brasile, a livello orario, ed è incluso nel path:

```
ai-lstm-service/app/data/energy_demand_hourly_brazil.csv
```

---

## 📈 Visualizzazione

Grafana e Kibana consentono l’analisi visiva di:

- Andamento dei consumi
- Generazione allerta
- Log applicativi
- Anomalie rilevate

---

## 🛠️ Manutenzione e Deploy

Questa piattaforma può essere facilmente estesa o distribuita in ambiente Kubernetes o cloud-native. Attualmente supporta deploy locale via Docker Compose per semplicità di testing e sviluppo.

---

## 👨‍💻 Contributi futuri

- [ ] Aggiunta API Gateway
- [ ] CI/CD pipeline completa con Jenkins
- [ ] Interfaccia web frontend (React/Vue)

---

## 📝 Licenza

Questo progetto è rilasciato sotto licenza MIT.
