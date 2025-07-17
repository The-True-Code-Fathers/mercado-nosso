import os
import asyncio
import pymongo
import tensorflow as tf
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import py_eureka_client.eureka_client as eureka_client

# --- Configuration ---
MODEL_PATH = os.path.join("model", "product_recommender.h5")
MONGO_URI = "mongodb://localhost:27017/"
EUREKA_SERVER = "http://localhost:8761/eureka"
SERVICE_HOST = "localhost"
SERVICE_PORT = 8000

# --- Pydantic Models for Request/Response ---
class RecommendationRequest(BaseModel):
    user_id: str
    
class RecommendationResponse(BaseModel):
    user_id: str
    recommended_products: list[str]

# --- Initialize Application and Connections ---
app = FastAPI(title="Recommendation Service")

# Load the TensorFlow model on startup
try:
    model = tf.keras.models.load_model(MODEL_PATH)
except IOError:
    model = None # Handle case where model doesn't exist yet
    print("Warning: Model file not found. Service will start without a model.")

# Connect to MongoDB
mongo_client = pymongo.MongoClient(MONGO_URI)
db = mongo_client.recommendation_cache
cache_collection = db.cache
new_products_collection = db.new_products

# --- Startup Event for Eureka Registration ---
@app.on_event("startup")
async def startup_event():
    # Register with Eureka server
    await eureka_client.init_async(
        eureka_server=EUREKA_SERVER,
        app_name="recommendation-service",
        instance_port=SERVICE_PORT,
        instance_host=SERVICE_HOST,
    )
    print("Service registered with Eureka.")
    if model is None:
        print("Model is not loaded. The '/recommend' endpoint will be unavailable.")

# --- API Endpoint ---
@app.post("/recommend", response_model=RecommendationResponse)
def get_recommendations(request: RecommendationRequest):
    if model is None:
        raise HTTPException(status_code=503, detail="Model is not loaded. Service is unavailable.")

    user_id = request.user_id

    # 1. Check Cache First
    cached_result = cache_collection.find_one({"_id": user_id})
    if cached_result:
        print(f"Cache HIT for user: {user_id}")
        return RecommendationResponse(
            user_id=user_id,
            recommended_products=cached_result["recommendations"]
        )
    
    print(f"Cache MISS for user: {user_id}")

    # 2. If not in cache, get prediction from model
    try:
        # NOTE: Adapt this part to your model's specific input/output format
        # This is a placeholder for your model prediction logic.
        # For example, you might need to convert the user_id to an integer index.
        user_input = preprocess_input(user_id) # You need to implement this function
        predictions = model.predict(user_input)
        recommended_ids = postprocess_output(predictions) # You need to implement this
        
    except Exception as e:
        # Example: Logic to handle a product ID that's not in the model's vocabulary
        print(f"Encountered a new entity not in the model: {e}")
        new_products_collection.update_one(
            {"_id": user_id}, 
            {"$set": {"_id": user_id}}, 
            upsert=True
        )
        raise HTTPException(status_code=404, detail=f"User or item '{user_id}' not found in model.")

    # 3. Store the new result in the cache
    cache_collection.insert_one({
        "_id": user_id,
        "recommendations": recommended_ids
    })

    return RecommendationResponse(user_id=user_id, recommended_products=recommended_ids)

# --- Helper Functions (Implement these based on your model) ---
def preprocess_input(user_id: str):
    """Converts user_id to the format your model expects."""
    # Example: return np.array([[user_id_to_int_mapping[user_id]]])
    raise NotImplementedError("Implement the preprocessing logic for your model input.")

def postprocess_output(predictions) -> list[str]:
    """Converts model output to a list of product IDs."""
    # Example: return [product_int_to_id_mapping[i] for i in top_k_indices]
    raise NotImplementedError("Implement the postprocessing logic for your model output.")
