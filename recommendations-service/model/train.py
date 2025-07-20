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
    1. Lê dados brutos do MongoDB.
    2. Gera recomendações.
    3. Salva as recomendações de volta no MongoDB.
    """
    print("Iniciando pipeline de recomendações...")

    # --- 1. Ler os dados de entrada do MongoDB ---
    print(f"Lendo dados da coleção '{INPUT_COLLECTION_NAME}' no MongoDB...")
    mongo_data_json = get_data_from_mongodb(collection_name=INPUT_COLLECTION_NAME)

    if mongo_data_json and "error" in mongo_data_json.lower():
        print(f"Erro ao carregar dados do MongoDB: {mongo_data_json}")
        return # Ou levante uma exceção, dependendo da sua estratégia de erro

    try:
        df_fragment = pd.read_json(io.StringIO(mongo_data_json))
        print(f"DataFrame carregado com {len(df_fragment)} itens da coleção '{INPUT_COLLECTION_NAME}'.")
    except Exception as e:
        print(f"Erro ao converter JSON para DataFrame: {e}")
        return

    # --- 2. Gerar as recomendações ---
    # As funções load_model e generate_recommendations_for_fragment devem existir no seu ambiente.
    print("Carregando modelo e gerando recomendações...")
    try:
        model_components = load_model("trained_model.pkl")
        recommendations_df = generate_recommendations_for_fragment(df_fragment, model_components)
        print(f"Recomendações geradas para {len(recommendations_df)} itens.")
    except Exception as e:
        print(f"Erro ao gerar recomendações: {e}")
        return

    # --- 3. Salvar as recomendações de volta no MongoDB ---
    # Converte o DataFrame de recomendações para uma lista de dicionários
    recommendations_list = recommendations_df.to_dict(orient='records')

    print(f"Salvando recomendações na coleção '{OUTPUT_COLLECTION_NAME}' no MongoDB...")
    save_result = save_data_to_mongodb(
        data_list=recommendations_list,
        collection_name=OUTPUT_COLLECTION_NAME,
        id_field=RECOMMENDATION_ID_FIELD # Use um campo que identifique unicamente a recomendação, ex: 'product_id'
    )

    if save_result["status"] == "success":
        print("Pipeline concluído com sucesso!")
        print(save_result["message"])
        # Opcional: imprimir detalhes da gravação
        # for detail in save_result["details"]:
        #     print(f"  - {detail}")
    else:
        print(f"Erro ao salvar recomendações: {save_result['message']}")
