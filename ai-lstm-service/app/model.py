import numpy as np
import tensorflow as tf
import joblib
from typing import List
from datetime import datetime

MODEL_PATH = "app/models/lstm_model.keras"
SCALER_PATH = "app/models/scaler.pkl"

def generate_time_features(dt: datetime):
    """Genera le feature temporali normalizzate da una datetime"""
    hour = dt.hour / 23.0
    day_of_week = dt.weekday() / 6.0
    month = (dt.month - 1) / 11.0
    return [hour, day_of_week, month]

def load_model_and_predict_for_datetimes(datetimes: List[str]) -> List[float]:
    """
    Predice il consumo energetico per una o più datetime (ISO format)

    Args:
        datetimes: lista di timestamp (es. 2025-06-03T15:00:00)

    Returns:
        Lista dei valori previsti di consumo energetico
    """
    try:
        model = tf.keras.models.load_model(MODEL_PATH)
        scaler = joblib.load(SCALER_PATH)

        predictions = []

        for dt_str in datetimes:
            dt = datetime.fromisoformat(dt_str)
            time_features = generate_time_features(dt)

            # Ripeti la sequenza 24 volte per formare l’input (shape: 1, 24, 4)
            input_sequence = [[0.0] + time_features] * 24  # dummy demand=0.0
            input_array = np.array(input_sequence).reshape(1, 24, 4)

            pred_scaled = model.predict(input_array, verbose=0)[0][0]
            pred_value = scaler.inverse_transform([[pred_scaled]])[0][0]
            predictions.append(round(pred_value, 2))

        return predictions

    except Exception as e:
        print(f"❌ Errore nella predizione: {str(e)}")
        raise

def get_model_info():
    """Restituisce informazioni sul modello"""
    try:
        model = tf.keras.models.load_model(MODEL_PATH)
        return {
            "input_shape": model.input_shape,
            "output_shape": model.output_shape,
            "parameters": model.count_params(),
            "window_size": 24,
            "features": ["demand_scaled", "hour", "day_of_week", "month"]
        }
    except Exception as e:
        return {"error": str(e)}