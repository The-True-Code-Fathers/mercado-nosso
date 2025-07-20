from model.recommendation import *
from model.train import *
import uvicorn
import asyncio
from fastapi import FastAPI, HTTPException
import py_eureka_client.eureka_client as eureka_client
import os
from contextlib import asynccontextmanager
from pydantic import BaseModel

EUREKA_SERVER = os.getenv("EUREKA_CLIENT_SERVICEURL_DEFAULTZONE", "http://eureka-server:8761/eureka/") # <--- CRITICAL CHANGE
APP_NAME = os.getenv("SPRING_APPLICATION_NAME", "recommendations-service")
SERVER_PORT = int(os.getenv("SERVER_PORT", 8086))



model_cache = {}

# --- Funções do Eureka Client (sem alterações) ---
async def start_eureka_client():
    retry_interval = 10
    while True:
        try:
            print(f"Tentando registrar no Eureka em: {EUREKA_SERVER}...", flush=True)
            await eureka_client.init_async(
                eureka_server=EUREKA_SERVER,
                app_name=APP_NAME,
                instance_port=SERVER_PORT,
            )
            print("✅ Registrado com sucesso no Eureka.", flush=True)
            break
        except Exception as e:
            print(f"Falha ao registrar no Eureka: {e}. Nova tentativa em {retry_interval} segundos...", flush=True)
            await asyncio.sleep(retry_interval)

# --- Lifespan para carregar o modelo na inicialização ---
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Lógica de inicialização (Startup)
    print("Iniciando o ciclo de vida da aplicação...", flush=True)
    
    # Carrega o modelo e armazena no cache
    print("Carregando o modelo de recomendação na memória...", flush=True)
    model_cache['components'] = load_model("trained_model.pkl")
    print("✅ Modelo carregado e pronto para uso.", flush=True)

    # Inicia o cliente Eureka
    asyncio.create_task(start_eureka_client())
    
    yield # A aplicação fica em execução aqui
    
    # Lógica de finalização (Shutdown)
    print("Finalizando o ciclo de vida da aplicação...", flush=True)
    await eureka_client.close_async()
    model_cache.clear()
    print("Registro no Eureka removido e cache do modelo limpo.", flush=True)

# --- Modelo Pydantic para o corpo da requisição do novo produto ---
class NewProduct(BaseModel):
    sku: str
    title: str
    price: float
    rating: float
    reviews: int
    boughtInLastMonth: int
    stock: int
    category: str
    productCondition: str
    sellerId: str
    isBestSeller: bool
    active: bool

# --- Instância do FastAPI com o lifespan ---
app = FastAPI(lifespan=lifespan)

print("DEBUG: FastAPI app definida com lifespan.", flush=True)

@app.get("/recommendations/{sku}")
async def get_recommendations_for_existing_sku(sku: str):
    """
    Retorna recomendações para um SKU existente.
    """
    if 'components' not in model_cache:
        raise HTTPException(status_code=503, detail="Modelo ainda não está pronto, tente novamente em alguns instantes.")
    
    try:
        recommendations = get_recommendations_for_sku(sku, model_cache['components'])
        return recommendations
    except ValueError as e:
        # Se o SKU não for encontrado, retorna um erro 404 (Not Found)
        raise HTTPException(status_code=404, detail=str(e))
    except Exception as e:
        # Para outros erros inesperados
        raise HTTPException(status_code=500, detail=f"Ocorreu um erro interno: {e}")

@app.post("/recommendations/new-product")
async def get_recommendations_for_new_product(product: NewProduct):
    """
    Gera e retorna recomendações para um novo produto enviado no corpo da requisição.
    """
    if 'components' not in model_cache:
        raise HTTPException(status_code=503, detail="Modelo ainda não está pronto, tente novamente em alguns instantes.")
    
    try:
        # Converte o modelo Pydantic para um dicionário
        product_dict = product.model_dump()
        recommendations = generate_recommendations_for_new_item(product_dict, model_cache['components'])
        return recommendations
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Falha ao gerar recomendações para o novo produto: {e}")

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
        uvicorn.run(app, host="0.0.0.0", port=SERVER_PORT, log_level="debug") # <--- ADD THIS
        print("DEBUG: Uvicorn server started (should stay running).", flush=True)
    except Exception as e:
        print(f"ERROR: Uvicorn failed to start or crashed immediately: {e}", flush=True)
        import sys
        sys.exit(1)