import uvicorn
import asyncio
from fastapi import FastAPI
import py_eureka_client.eureka_client as eureka_client
import os

app = FastAPI()

APP_NAME = os.getenv("SPRING_APPLICATION_NAME", "recommendations-service")
SERVER_PORT = int(os.getenv("SERVER_PORT", 8086))
EUREKA_SERVER = os.getenv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://eureka-server:8761/eureka/")

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
    asyncio.create_task(start_eureka_client())

async def start_eureka_client():
    """Initializes and starts the Eureka client with a retry mechanism."""
    retry_interval = 5  # In seconds
    while True:
        try:
            print("Attempting to register with Eureka...")
            await eureka_client.init_async(
                eureka_server=EUREKA_SERVER,
                app_name=APP_NAME,
                instance_port=SERVER_PORT,
            )
            print("Successfully registered with Eureka.")
            break  # Exit the loop if registration is successful
        except Exception as e:
            print(f"Failed to register with Eureka: {e}. Retrying in {retry_interval} seconds...")
            await asyncio.sleep(retry_interval)

if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=SERVER_PORT, reload=True)
