from confluent_kafka import Producer
import json
from datetime import datetime

KAFKA_BOOTSTRAP_SERVERS = 'kafka:9092'
TOPIC = 'alert-events'

producer = Producer({'bootstrap.servers': KAFKA_BOOTSTRAP_SERVERS})

def send_lstm_alerts(predictions: list[float], datetimes: list[str]):
    for prediction, dt in zip(predictions, datetimes):
        event = {
            "source": "AI-LSTM-SERVICE",
            "timestamp": dt,  # previsioni in avanti → timestamp futuro
            "message": f"Predicted demand at {dt} = {prediction}",
            "hourlyEnergyConsumption": prediction
        }

        try:
            producer.produce(TOPIC, key="alert", value=json.dumps(event))
            print(f"✅ Alert inviato: {event}")
        except Exception as e:
            print(f"❌ Errore nell'invio alert: {e}")

    producer.flush()