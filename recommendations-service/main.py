from model.recommendation import *
from model.train import *
import uvicorn
import asyncio
from fastapi import FastAPI, HTTPException
import py_eureka_client.eureka_client as eureka_client
import os


EUREKA_SERVER = os.getenv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://eureka-server:8761/eureka/") # <--- CRITICAL CHANGE
APP_NAME = os.getenv("SPRING_APPLICATION_NAME", "recommendations-service")
SERVER_PORT = int(os.getenv("SERVER_PORT", 8086))

app = FastAPI()

print("DEBUG: FastAPI app defined.", flush=True)

@app.get("/recommendations")
async def get_recommendations():
    # Placeholder for recommendations logic
    return {
        "service": "Recommendation Service",
        "status": "Running",
        "recommendations": ["Product A", "Product B", "Product C"]
    }

@app.on_event("startup")
async def startup_event():
    """Run the Eureka client when the application starts."""
    print("DEBUG: FastAPI startup event triggered.", flush=True)
    # asyncio.create_task(start_eureka_client())
    print("DEBUG: Eureka client task created.", flush=True)

async def start_eureka_client():
    """Initializes and starts the Eureka client with a retry mechanism."""
    retry_interval = 5  # In seconds
    while True:
        try:
            print("Attempting to register with Eureka...", flush=True)
            await eureka_client.init_async(
                eureka_server=EUREKA_SERVER,
                app_name=APP_NAME,
                instance_port=SERVER_PORT,
                instance_host=APP_NAME
            )
            print("Successfully registered with Eureka.", flush=True)
            break  # Exit the loop if registration is successful
        except Exception as e:
            print(f"Failed to register with Eureka: {e}. Retrying in {retry_interval} seconds...", flush=True)
            await asyncio.sleep(retry_interval)

@app.post("/generate-recommendations/")
async def generate_recommendations():
    """
    Triggers the recommendation generation pipeline.
    This will read data, generate recommendations, and save them to MongoDB.
    """
    print("API Call: Initiating recommendation generation pipeline...", flush=True)
    try:
        # Run the synchronous pipeline function in a thread pool to not block the event loop
        # For long-running tasks, it's essential to use run_in_executor
        # If execute_recommendation_pipeline is itself async, you can await it directly.
        # Assuming it's synchronous for now:
        await asyncio.to_thread(execute_recommendation_pipeline)
        # For Python versions < 3.9, use:
        # loop = asyncio.get_event_loop()
        # await loop.run_in_executor(None, execute_recommendation_pipeline)
        
        return {"status": "success", "message": "Recommendation pipeline triggered successfully. Check logs for details."}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to trigger recommendation pipeline: {e}")

if __name__ == '__main__':
    print("DEBUG: __name__ is __main__, starting Uvicorn...", flush=True)
    try:
        uvicorn.run(app, host="0.0.0.0", port=SERVER_PORT)
        print("DEBUG: Uvicorn server started (should stay running).", flush=True) # This line usually won't be seen if server is running
    except Exception as e:
        print(f"ERROR: Uvicorn failed to start or crashed immediately: {e}", flush=True) # NEW ERROR CATCH
        # It's good to re-raise or exit with an error code if this happens
        import sys
        sys.exit(1)