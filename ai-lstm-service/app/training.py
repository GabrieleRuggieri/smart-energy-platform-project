import os
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
import joblib

MODEL_PATH = "app/models/lstm_model.keras"
SCALER_PATH = "app/models/scaler.pkl"
CSV_PATH = "app/data/energy_demand_hourly_brazil.csv"

def create_sequences_with_time_features(df, window_size):
    """Crea sequenze con feature temporali"""
    feature_columns = ["demand_scaled", "hour", "day_of_week", "month"]
    data = df[feature_columns].values
    X, y = [], []
    for i in range(window_size, len(data)):
        X.append(data[i - window_size:i])
        y.append(data[i][0])  # Solo demand_scaled come target
    return np.array(X), np.array(y)

def train_model_if_needed():
    if os.path.exists(MODEL_PATH) and os.path.exists(SCALER_PATH):
        return

    print("🔧 Inizio training del modello LSTM...")
    os.makedirs("app/models", exist_ok=True)

    df = pd.read_csv(CSV_PATH)
    df.columns = ["datetime", "demand"]
    df["datetime"] = pd.to_datetime(df["datetime"])
    df["demand"] = df["demand"].astype(float)

    # Rimuovi outlier
    Q1 = df["demand"].quantile(0.25)
    Q3 = df["demand"].quantile(0.75)
    IQR = Q3 - Q1
    df = df[(df["demand"] >= Q1 - 1.5 * IQR) & (df["demand"] <= Q3 + 1.5 * IQR)]

    # Normalizza solo demand
    scaler = MinMaxScaler(feature_range=(0, 1))
    df["demand_scaled"] = scaler.fit_transform(df["demand"].values.reshape(-1, 1))

    # Estrai feature temporali
    df["hour"] = df["datetime"].dt.hour / 23.0
    df["day_of_week"] = df["datetime"].dt.weekday / 6.0
    df["month"] = (df["datetime"].dt.month - 1) / 11.0

    # Crea sequenze con finestra mobile
    window_size = 24
    X, y = create_sequences_with_time_features(df, window_size)

    X_train, X_val, y_train, y_val = train_test_split(
        X, y, test_size=0.2, random_state=42, shuffle=False
    )

    # Modello LSTM
    model = tf.keras.Sequential([
        tf.keras.layers.LSTM(64, return_sequences=True, input_shape=(window_size, X.shape[2])),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.LSTM(32),
        tf.keras.layers.Dropout(0.2),
        tf.keras.layers.Dense(16, activation='relu'),
        tf.keras.layers.Dense(1)
    ])

    model.compile(optimizer='adam', loss='mse', metrics=['mae'])

    early_stopping = tf.keras.callbacks.EarlyStopping(
        monitor='val_loss', patience=5, restore_best_weights=True
    )

    history = model.fit(
        X_train, y_train,
        validation_data=(X_val, y_val),
        epochs=50,
        batch_size=32,
        callbacks=[early_stopping],
        verbose=1
    )

    model.save(MODEL_PATH)
    joblib.dump(scaler, SCALER_PATH)

    print("✅ Modello addestrato e salvato.")
    print(f"📊 Loss finale: {history.history['loss'][-1]:.4f}")
    print(f"📊 Val Loss finale: {history.history['val_loss'][-1]:.4f}")
    print(f"📊 Val Loss finale: {history.history['val_loss'][-1]:.4f}")