# ⚡ Smart Energy Platform

Smart Energy Platform è un sistema distribuito a microservizi per il monitoraggio e l’analisi in tempo reale dei consumi energetici provenienti da dispositivi smart. Progettato per essere scalabile, modulare e facilmente integrabile, il sistema raccoglie, elabora, memorizza e analizza dati di consumo energetico fornendo alert, logging centralizzato e supporto all’analisi.

---

## 🚀 Obiettivi del Progetto

- Monitoraggio in tempo reale dei consumi energetici.
- Architettura a microservizi per scalabilità e manutenibilità.
- Logging centralizzato e sistema di allerta per consumi anomali.
- CI/CD automatizzata con Jenkins e SonarQube.
- Analisi e aggregazione dei consumi tramite Kafka ed Elasticsearch.
- Visualizzazione dei log e delle metriche via Kibana e Grafana.

---

## 🧱 Architettura

Il progetto è organizzato in più microservizi indipendenti, ognuno responsabile di un dominio specifico.

### Microservizi

| Microservizio         | Descrizione |
|------------------------|-------------|
| **gateway-service**    | API Gateway centralizzato per routing, rate limiting e sicurezza. |
| **energy-service**     | Ingestione e persistenza dei dati di consumo energetico. |
| **alert-service**      | Generazione di alert in base a soglie e regole personalizzate. |
| **analytics-service**  | Analisi storica e aggregata dei dati di consumo. |
| **logging-service**    | Raccolta e centralizzazione dei log strutturati tramite ELK stack. |

---

## 🛠️ Tecnologie Utilizzate

- **Java 17** + **Quarkus**
- **Apache Kafka** per comunicazione asincrona
- **Docker** e **Docker Compose** per containerizzazione
- **Elasticsearch**, **Kibana** per logging avanzato
- **JUnit 5**, **Mockito** per testing
- **Jenkins** per CI/CD
- **SonarQube** per analisi della qualità del codice

---

## 📦 Struttura del Repository

```
smart-energy-platform/
├── gateway-service/
├── energy-service/
├── alert-service/
├── analytics-service/
├── logging-service/
├── docker-compose.yml
└── README.md
```

Ogni microservizio include:

- `src/main/java` con Controller, Service, Model
- `application.yml` con configurazioni di base
- `pom.xml` con dipendenze specifiche
- `Dockerfile` per il build containerizzato
- `Jenkinsfile` per pipeline CI/CD
- Test unitari e/o d'integrazione

---

## 🐳 Avvio Locale

> È necessario avere installato: Docker, Docker Compose e Maven.

### 1. Avvio dei servizi esterni

```bash
docker-compose up --build
```

```bash
docker logs -f filebeat
```

IMPORTANTE
chmod 644 ./elk/filebeat.yml


Avvia i seguenti componenti:
- Kafka + Zookeeper
- Elasticsearch (porta 9200)
- Kibana (porta 5601)

### 2. Build dei microservizi

```bash
cd energy-service
mvn clean install
docker build -t energy-service:latest .
```

Ripeti per ciascun microservizio.

### 3. Esecuzione manuale

```bash
docker run -p 8080:8080 energy-service:latest
```

---

## 🧪 Testing

Ogni servizio include test unitari:

```bash
mvn test
```

Struttura dei test:
- JUnit 5 per test unitari
- Mockito per mock dei componenti
- Testcontainers (opzionale) per test d’integrazione con DB/Kafka

---

## 🔁 Pipeline CI/CD

Ogni microservizio include un `Jenkinsfile` che automatizza:

- Build e compilazione
- Esecuzione dei test
- Analisi con SonarQube
- Build dell’immagine Docker

Esempio:

```groovy
pipeline {
  agent any
  stages {
    stage('Build') {
      steps { sh 'mvn clean package' }
    }
    stage('Test') {
      steps { sh 'mvn test' }
    }
    stage('Sonar') {
      steps {
        withSonarQubeEnv('MySonarQube') {
          sh 'mvn sonar:sonar'
        }
      }
    }
    stage('Docker') {
      steps { sh 'docker build -t energy-service:latest .' }
    }
  }
}
```

---

## 📈 Logging & Monitoraggio

- I microservizi generano log in formato JSON
- I log sono inviati a Elasticsearch
- Kibana consente la visualizzazione, filtro e analisi
- Dashboard configurabile su `http://localhost:5601`

---

## 📝 Licenza

Questo progetto è rilasciato sotto licenza MIT.  
Vedi il file [LICENSE](./LICENSE) per i dettagli.
