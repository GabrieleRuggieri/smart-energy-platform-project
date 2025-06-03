from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field, validator
from typing import List, Optional
import uvicorn
from app.model import load_model_and_predict_for_datetimes, get_model_info
from app.training import train_model_if_needed
from app.alert import send_alert
from datetime import datetime

app = FastAPI(
    title="Energy Demand Prediction API",
    description="API per previsioni di consumo energetico in base a date/ora",
    version="2.0.0"
)

class PredictRequest(BaseModel):
    datetimes: List[str] = Field(
        ...,
        description="Lista di date/ore (formato ISO: es. 2025-06-03T14:00:00)",
        min_items=1
    )

    @validator('datetimes', each_item=True)
    def validate_datetime_format(cls, dt_str):
        try:
            datetime.fromisoformat(dt_str)
            return dt_str
        except Exception:
            raise ValueError(f"Formato datetime non valido: {dt_str}")

class PredictResponse(BaseModel):
    predictions: List[float]
    requested_datetimes: List[str]

class ModelInfoResponse(BaseModel):
    model_info: dict
    status: str

@app.on_event("startup")
async def startup_event():
    print("🚀 Avvio API...")
    train_model_if_needed()
    print("✅ API pronta!")

@app.post("/predict", response_model=PredictResponse)
async def predict_demand(request: PredictRequest):
    try:
        predictions = load_model_and_predict_for_datetimes(request.datetimes)
        send_alert(predictions[0])  # opzionale
        return PredictResponse(
            predictions=predictions,
            requested_datetimes=request.datetimes
        )
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

@app.get("/model/info", response_model=ModelInfoResponse)
async def model_info():
    try:
        info = get_model_info()
        return ModelInfoResponse(model_info=info, status="active")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Errore: {str(e)}")

@app.get("/health")
async def health_check():
    return {"status": "healthy", "message": "API operativa"}

@app.get("/")
async def root():
    return {
        "message": "Energy Demand Prediction API",
        "version": "2.0.0",
        "endpoints": {
            "predict": "/predict",
            "model_info": "/model/info",
            "health": "/health",
            "docs": "/docs"
        }
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)