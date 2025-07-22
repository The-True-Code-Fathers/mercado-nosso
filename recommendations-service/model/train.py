from .recommendation import *

MONGO_HOST = 'listings-service-mongodb' # Nome do serviço do MongoDB no docker-compose.yml
MONGO_PORT = 27017
MONGO_DB_NAME = 'listings-service-mongodb'    
INPUT_COLLECTION_NAME = 'listings' # Exemplo: Sua coleção de produtos/listings


# df_full = pd.read_json(r"C:\Users\Aluno\mercado-nosso-database\bd_completa.json")  # Load the full dataset (1.08 million items)
# train_and_save_model(df_full, "trained_model.pkl")

# df_fragment = pd.read_json(r"C:\Users\nelso\df_data_mercado_nosso\listing.json")  # Load the 6k-item fragment
# model_components = load_model("trained_model.pkl")
# recommendations = generate_recommendations_for_fragment(df_fragment, model_components)
# print(recommendations)

# save_recommendations_to_json(recommendations, "fragment_recommendations.json")

# new_product = {
#     "sku": "NEW-SKU-123",
#     "title": "A Brand New High Quality Product",
#     "price": 99.99,
#     "rating": 4.8,
#     "reviews": 150,
#     "boughtInLastMonth": 75,
#     "stock": 200,
#     "category": "Electronics",
#     "productCondition": "New",
#     "sellerId": "SELLER-XYZ",
#     "isBestSeller": True,
#     "active": True
# }

# model_components = load_model("trained_model.pkl")
# new_product_results = generate_recommendations_for_new_item(new_product, model_components)
# save_recommendations_to_json([new_product_results], "new_product_recommendation.json")

# import pickle

# # Load the model
# with open("trained_model.pkl", "rb") as f:
#     model_components = pickle.load(f)

# # Print the keys (the names of the items stored inside)
# print(list(model_components.keys()))

def load_model(model_path: str):
    """
    Carrega o modelo treinado.
    Substitua esta função pela sua lógica real de carregamento do modelo.
    """
    print(f"DEBUG: Carregando modelo de: {model_path}")
    # Adjust this path based on where you COPY the model and where train.py runs
    # If train.py is in /app/model and model/trained_model.pkl is copied to /app/model/
    # then model_path should just be "trained_model.pkl"
    # If trained_model.pkl is in /app/model/ and train.py is in /app, then use "model/trained_model.pkl"
    
    # Assuming 'trained_model.pkl' is in the same directory as 'train.py'
    # because of `COPY model/trained_model.pkl model/` and `working_dir: /app/model`
    # in docker-compose.yml, or if `train.py` is directly in `/app` and `trained_model.pkl` also.
    
    # The most robust way is to use an absolute path or a path relative to the script's location
    current_dir = os.path.dirname(os.path.abspath(__file__))
    full_model_path = os.path.join(current_dir, model_path) # Assumes model_path="trained_model.pkl"
    
    # Example: Load the pickle file
    try:
        with open(full_model_path, 'rb') as f:
            model = pickle.load(f)
        print(f"DEBUG: Modelo carregado com sucesso de: {full_model_path}")
        return model
    except FileNotFoundError:
        print(f"ERROR: Modelo não encontrado em: {full_model_path}")
        raise
    except Exception as e:
        print(f"ERROR: Erro ao carregar o modelo de {full_model_path}: {e}")
        raise



def execute_recommendation_pipeline():
    """
    Executa o pipeline completo:
    """
    print("DEBUG: Pipeline function ENTERED.", flush=True)
    print("Iniciando pipeline de recomendações...", flush=True)

    try:
        # --- 1. Ler os dados de entrada do MongoDB ---
        print(f"DEBUG: Step 1: Attempting to read data from '{INPUT_COLLECTION_NAME}'.", flush=True)
        mongo_data_list = get_data_from_mongodb(collection_name=INPUT_COLLECTION_NAME)

        print("DEBUG: Step 1: Data fetched from MongoDB. Attempting to convert to DataFrame.", flush=True)
        df_raw = pd.DataFrame(mongo_data_list) # Renomeado para df_raw para clareza

        # ==========================================================
        # ---> PONTO DE INTEGRAÇÃO AQUI <---
        # 2. Validar e limpar os dados antes de prosseguir
        print("DEBUG: Step 1.5: Validating and cleaning data.", flush=True)
        df_cleaned = validate_and_clean_dataframe(df_raw) # Chama a função de limpeza
        # ==========================================================

        if df_cleaned.empty:
            print("WARNING: No data left after cleaning process. Exiting pipeline.", flush=True)
            return

        print(f"DEBUG: Step 1: DataFrame loaded and cleaned with {len(df_cleaned)} items.", flush=True)
        print(f"DataFrame carregado com {len(df_cleaned)} itens da coleção '{INPUT_COLLECTION_NAME}'.", flush=True)

        # --- 3. Gerar as recomendações ---
        print("DEBUG: Step 2: Attempting to load model and generate recommendations.", flush=True)
        model_components = load_model("trained_model.pkl") # Use o load_model do seu arquivo
        print("DEBUG: Step 2: Model loaded. Generating recommendations.", flush=True)

        # PASSE O DATAFRAME LIMPO (df_cleaned) PARA A FUNÇÃO
        recommendations_df = generate_recommendations_for_fragment(df_cleaned, model_components)
        print(f"DEBUG: Step 2: Recommendations generated for {len(recommendations_df)} items.", flush=True)
        print(f"Recomendações geradas para {len(recommendations_df)} itens.", flush=True)

        # --- 4. Salvar as recomendações de volta no MongoDB ---
        print("DEBUG: Step 3: Attempting to save recommendations to MongoDB.", flush=True)
        recommendations_list = recommendations_df.to_dict(orient='records')
        save_result = save_data_to_mongodb(
            data_list=recommendations_list,
            collection_name=OUTPUT_COLLECTION_NAME,
            id_field=RECOMMENDATION_ID_FIELD,
            mongo_host=MONGO_HOST_OUTPUT,
            mongo_db_name=MONGO_DB_NAME_OUTPUT
        )

        if save_result["status"] == "success":
            print("Pipeline concluído com sucesso!", flush=True)
            print(save_result["message"], flush=True)
        else:
            print(f"ERROR: Step 3 failed - Error saving recommendations: {save_result['message']}", flush=True)

    except Exception as e:
        print(f"ERROR: Pipeline failed critically. Error: {e}", flush=True)
        raise

    print("DEBUG: Pipeline function EXITED.", flush=True)
