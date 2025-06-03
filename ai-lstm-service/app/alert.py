import requests

ALERT_SERVICE_URL = "http://alert-service:8081/alerts"

def send_alert(prediction: float):
    payload = {"message": f"Predicted demand: {prediction}"}
    try:
        response = requests.post(ALERT_SERVICE_URL, json=payload)
        response.raise_for_status()
    except Exception as e:
        print(f"❌ Errore nell'invio alert: {e}")