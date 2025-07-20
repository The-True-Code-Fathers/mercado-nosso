import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder, StandardScaler, MinMaxScaler
from sklearn.neighbors import NearestNeighbors
from sklearn.feature_extraction.text import TfidfVectorizer
from keras.models import Model
from keras.layers import Input, Dense, Dropout
import re
from textblob import TextBlob  # Para análise de sentimento (opcional)
import os
import glob
import random
import pickle
import json
import io
import pymongo
from pymongo import MongoClient
from bson.json_util import dumps
from bson.objectid import ObjectId

MONGO_HOST_INPUT = 'listings-service-mongodb' # Nome do serviço do MongoDB no docker-compose.yml
MONGO_PORT = 27017
MONGO_DB_NAME_INPUT = 'listings-service-mongodb'    
INPUT_COLLECTION_NAME = 'listings' # Exemplo: Sua coleção de produtos/listings

MONGO_HOST_OUTPUT = 'recommendations-service-mongodb' # Service name for recommendations MongoDB
MONGO_DB_NAME_OUTPUT = 'my_recommendation_database' # <--- LET'S MAKE THIS A CLEAR NEW NAME!
OUTPUT_COLLECTION_NAME = 'generated_recommendations' # <--- And let's make this explicit too!
RECOMMENDATION_ID_FIELD = 'sku' # Field used as ID for recommendations único no seu DataFrame de recomendações

def preprocess_features(df):
    """
    Preprocessa todas as features disponíveis para o sistema de recomendação
    """
    df_processed = df.copy()
    
    # ==================== FEATURES NUMÉRICAS ====================
    # Features numéricas diretas - muito importantes!
    numerical_features = ['price', 'rating', 'reviews', 'boughtInLastMonth', 
                         'stock']
    
    # print("✅ Features numéricas incluídas:", numerical_features)
    
    # ==================== FEATURES CATEGÓRICAS ====================
    # 1. Category - já estava sendo usada
    label_encoder_cat = LabelEncoder()
    df_processed['category_encoded'] = label_encoder_cat.fit_transform(df['category'])
    
    # 2. ProductCondition - muito importante para recomendações
    label_encoder_condition = LabelEncoder()
    df_processed['productCondition_encoded'] = label_encoder_condition.fit_transform(df['productCondition'])
    
    # 3. SellerId - pode ajudar a recomendar produtos do mesmo vendedor
    label_encoder_seller = LabelEncoder()
    df_processed['sellerId_encoded'] = label_encoder_seller.fit_transform(df['sellerId'])
    
    # ==================== FEATURES BOOLEANAS ====================
    # Converter para 0 e 1
    df_processed['isBestSeller_encoded'] = df['isBestSeller'].astype(int)
    df_processed['active_encoded'] = df['active'].astype(int)
    
    # ==================== FEATURES DERIVADAS ====================
    # 1. Popularidade geral (combinação de reviews e rating)
    df_processed['popularity_score'] = (df['rating'] * np.log1p(df['reviews'])).fillna(0)
    
    
    # 4. Disponibilidade (stock > 0)
    df_processed['in_stock'] = (df['stock'] > 0).astype(int)
    
    # 5. Faixa de preço (categorização)
    df_processed['price_range'] = pd.cut(df['price'], 
                                       bins=[0, 50, 100, 200, 500, float('inf')], 
                                       labels=[0, 1, 2, 3, 4]).cat.add_categories([5]).fillna(5).astype(int)
    
    # 6. Categoria de rating
    df_processed['rating_category'] = pd.cut(df['rating'], 
                                           bins=[0, 3, 4, 4.5, 5], 
                                           labels=[0, 1, 2, 3]).cat.add_categories([4]).fillna(4).astype(int)
    
    categorical_features = ['category_encoded', 'productCondition_encoded', 
                          'sellerId_encoded', 'isBestSeller_encoded', 'active_encoded',
                          'in_stock', 'price_range', 'rating_category']
    
    # print("✅ Features categóricas incluídas:", categorical_features)
    
    # ==================== FEATURES DERIVADAS AVANÇADAS ====================
    derived_features = ['popularity_score']
    # print("✅ Features derivadas incluídas:", derived_features)
    
    encoders = {
    'category': label_encoder_cat,
    'productCondition': label_encoder_condition,
    'sellerId': label_encoder_seller
}
    return df_processed, numerical_features, categorical_features, derived_features, encoders

def process_text_features(df, max_features=100):
    """
    Processa features de texto (title e description) usando TF-IDF
    """
    # print("🔤 Processando features de texto...")
    
    # Limpar e combinar título e descrição
    df['combined_text'] = df['title'].fillna('').fillna('')
    
    # Preprocessing básico de texto
    def clean_text(text):
        text = str(text).lower()
        text = re.sub(r'[^a-zA-Z\s]', '', text)
        return text
    
    df['combined_text_clean'] = df['combined_text'].apply(clean_text)
    
    # TF-IDF para extrair features importantes do texto
    tfidf = TfidfVectorizer(max_features=max_features, 
                           stop_words='english',
                           ngram_range=(1, 2))
    
    text_features = tfidf.fit_transform(df['combined_text_clean']).toarray()
    
    # Criar DataFrame com features de texto
    text_feature_names = [f'text_feature_{i}' for i in range(text_features.shape[1])]
    text_df = pd.DataFrame(text_features, columns=text_feature_names)
    
    # print(f"✅ {text_features.shape[1]} features de texto extraídas")
    
    return text_df, tfidf

def create_enhanced_recommendation_system(df, use_text_features=True, text_max_features=50):
    """
    Cria um sistema de recomendação avançado usando todas as features disponíveis
    """
    print("🚀 Criando sistema de recomendação melhorado...")
    
    # Preprocessar features
    df_processed, numerical_features, categorical_features, derived_features, label_encoders = preprocess_features(df)
    
    # Escalar features numéricas
    scaler = StandardScaler()
    numerical_data = scaler.fit_transform(df_processed[numerical_features + derived_features])
    
    # Combinar features numéricas e categóricas
    categorical_data = df_processed[categorical_features].values
    
    # Combinar todas as features
    if use_text_features:
        text_df, tfidf = process_text_features(df, max_features=text_max_features)
        X = np.concatenate([numerical_data, categorical_data, text_df.values], axis=1)
        print(f"✅ Features totais: {X.shape[1]} (numéricas: {len(numerical_features + derived_features)}, categóricas: {len(categorical_features)}, texto: {text_df.shape[1]})")
    else:
        X = np.concatenate([numerical_data, categorical_data], axis=1)
        tfidf = None
        print(f"✅ Features totais: {X.shape[1]} (numéricas: {len(numerical_features + derived_features)}, categóricas: {len(categorical_features)})")
    
    # Autoencoder melhorado com normalização
    input_layer = Input(shape=(X.shape[1],))
    encoded = Dense(256, activation='relu')(input_layer)
    encoded = Dropout(0.3)(encoded)
    encoded = Dense(128, activation='relu')(encoded)
    encoded = Dropout(0.3)(encoded)
    encoded = Dense(64, activation='relu')(encoded)  # Camada de codificação
    
    decoded = Dense(128, activation='relu')(encoded)
    decoded = Dropout(0.3)(decoded)
    decoded = Dense(256, activation='relu')(decoded)
    decoded = Dense(X.shape[1], activation='linear')(decoded)  # Mudança para linear
    
    autoencoder = Model(input_layer, decoded)
    encoder = Model(input_layer, encoded)
    
    # Compilar e treinar com learning rate menor
    from keras.optimizers import Adam
    autoencoder.compile(optimizer=Adam(learning_rate=0.001), loss='mse')
    print("🏋️ Treinando autoencoder...")
    epochs = min(20, max(5, 1000 // len(df)))  # Adaptativo baseado no tamanho
    batch_size = min(128, len(df) // 4)
    autoencoder.fit(X, X, epochs=epochs, batch_size=batch_size, shuffle=True, verbose=1, validation_split=0.2)
    
    # Obter representação compacta
    X_reduced = encoder.predict(X)
    
    # Sistema KNN com métricas diferentes
    knn_cosine = NearestNeighbors(n_neighbors=20, metric='cosine')
    knn_cosine.fit(X_reduced)
    
    # Adicionar KNN euclidiano como alternativa
    knn_euclidean = NearestNeighbors(n_neighbors=20, metric='euclidean')
    knn_euclidean.fit(X_reduced)
    
    return {
        'knn_cosine': knn_cosine,
        'knn_euclidean': knn_euclidean,
        'encoder': encoder,
        'scaler': scaler,
        'X_reduced': X_reduced,
        'X_original': X,
        'df_processed': df_processed,
        'numerical_features': numerical_features,
        'categorical_features': categorical_features,
        'derived_features': derived_features,
        'tfidf': tfidf,
        'label_encoders': label_encoders,
        'feature_names': {
            'numerical': numerical_features + derived_features,
            'categorical': categorical_features,
            'text_features': text_max_features if use_text_features else 0
        }
    }

def get_smart_recommendations(product_index, model_components, n_recommendations=5):
    """
    Gets recommendations using the pre-computed data inside model_components.
    """
    # Use the correct key for the KNN model
    knn = model_components.get('knn_cosine') or model_components.get('knn') 
    
    X_reduced = model_components['X_reduced']
    df_full = model_components['df_processed']
    
    # Get the embedding for the target product
    product_embedding = X_reduced[product_index].reshape(1, -1)
    
    # Find the nearest neighbors
    distances, indices = knn.kneighbors(product_embedding, n_neighbors=n_recommendations + 1)
    
    # The first item is always the product itself, so we skip it (start from index 1)
    rec_indices = indices[0][1:]
    
    # Get the SKUs of the recommended products from the full DataFrame
    rec_skus = df_full.iloc[rec_indices]['sku'].tolist()
    
    return rec_skus

def display_recommendations(product_index, df, recommendations, similarities):
    """
    Exibe as recomendações de forma organizada
    """
    original = df.iloc[product_index]
    
    # print("=" * 80)
    # print("🎯 PRODUTO ORIGINAL")
    # print("=" * 80)
    # print(f"Título: {original['title']}")
    # print(f"Categoria: {original['category']}")
    # print(f"Preço: R$ {original['price']:.2f}")
    # print(f"Rating: {original['rating']}/5.0 ({original['reviews']} reviews)")
    # print(f"Vendido por: {original['sellerId']}")
    # print(f"Best Seller: {'Sim' if original['isBestSeller'] else 'Não'}")
    # print(f"Compras último mês: {original['boughtInLastMonth']}")
    
    # print("\n" + "=" * 80)
    # print("🔥 PRODUTOS RECOMENDADOS")
    # print("=" * 80)
    
    for i, (idx, similarity) in enumerate(zip(recommendations, similarities)):
        product = df.iloc[idx]
        # print(f"\n{i+1}. {product['title']}")
        # print(f"   📂 Categoria: {product['category']}")
        # print(f"   💰 Preço: R$ {product['price']:.2f}")
        # print(f"   ⭐ Rating: {product['rating']}/5.0 ({product['reviews']} reviews)")
        # print(f"   🏪 Vendedor: {product['sellerId']}")
        # print(f"   🔥 Best Seller: {'Sim' if product['isBestSeller'] else 'Não'}")
        # print(f"   🎯 Similaridade: {similarity:.3f}")

def get_product_index_by_sku(df, sku):
    """
    Maps an SKU to the corresponding product index in the DataFrame.
    """
    try:
        return df[df['sku'] == sku].index[0]
    except IndexError:
        raise ValueError(f"SKU '{sku}' not found in the dataset.")

def run_enhanced_recommendation_example(df, sku):
    """
    Generates product recommendations based on an SKU input.
    """
    print("🔧 Inicializando sistema de recomendação avançado...")
    
    # Criar o sistema
    model_components = create_enhanced_recommendation_system(df, use_text_features=True)
    
    # Map SKU to product index
    try:
        product_index = get_product_index_by_sku(df, sku)
    except ValueError as e:
        print(e)
        return None

    # Generate recommendations
    print("\n" + "="*60)
    print(f"EXEMPLO: Recomendação para SKU '{sku}'")
    print("="*60)
    
    recommendations, similarities = get_smart_recommendations(
        product_index, df, model_components, n_recommendations=3
    )
    
    display_recommendations(product_index, df, recommendations, similarities)
    
    return model_components

def train_and_save_model(df, model_path):
    """
    Trains the recommendation model and saves it to disk.
    
    Args:
        df (pd.DataFrame): The DataFrame containing the full dataset (1.08 million items).
        model_path (str): Path to save the trained model components.
    """
    print("🔧 Training the recommendation model...")
    model_components = create_enhanced_recommendation_system(df, use_text_features=True)
    
    # Save the trained model components to disk
    with open(model_path, "wb") as f:
        pickle.dump(model_components, f)
    print(f"✅ Model saved to {model_path}")


def load_model(model_path):
    """
    Loads the trained recommendation model from disk.
    
    Args:
        model_path (str): Path to the saved model components.
    
    Returns:
        dict: The loaded model components.
    """
    print(f"DEBUG: Carregando modelo de: {model_path}", flush=True)
    print("🔧 Loading the recommendation model...", flush=True)
    with open(model_path, "rb") as f:
        model_components = pickle.load(f)
    print("✅ Model loaded successfully", flush=True)
    return model_components

def get_recommendations_for_sku(sku: str, model_components: dict, n_recommendations: int = 3) -> dict:
    """
    Busca recomendações para um único SKU existente no modelo treinado.

    Args:
        sku (str): O SKU do produto para o qual queremos recomendações.
        model_components (dict): O dicionário com os componentes do modelo carregado.
        n_recommendations (int): O número de recomendações a serem retornadas.

    Returns:
        dict: Um dicionário com o SKU original e a lista de SKUs recomendados.
    
    Raises:
        ValueError: Se o SKU não for encontrado no dataset do modelo.
    """
    print(f"Buscando recomendações para o SKU existente: {sku}", flush=True)
    df_full_processed = model_components['df_processed']
    
    # Cria um mapa de SKU para índice para busca rápida
    sku_to_index = {s: i for i, s in enumerate(df_full_processed['sku'])}
    
    product_index = sku_to_index.get(sku)

    # Verifica se o SKU foi encontrado
    if product_index is None:
        raise ValueError(f"SKU '{sku}' não encontrado no dataset do modelo treinado.")

    # Usa a função que já tínhamos para buscar os vizinhos mais próximos
    rec_skus = get_smart_recommendations(
        product_index,
        model_components,
        n_recommendations=n_recommendations
    )

    return {
        "sku": sku,
        "recommendations": rec_skus
    }

def generate_recommendations_for_fragment(df_fragment, model_components, n_recommendations=3):
    """
    Generates recommendations for a fragment with progress logging.
    """
    print(f"\n🧠 Iniciando geração de recomendações filtradas para {len(df_fragment)} itens...", flush=True)

    # --- PASSO 1: Obter os embeddings apenas para os SKUs do fragmento ---
    df_full_processed = model_components['df_processed']
    X_reduced = model_components['X_reduced']
    
    # Criar um mapa de SKU para índice do dataset completo para busca rápida
    sku_to_full_index = pd.Series(df_full_processed.index, index=df_full_processed.sku)
    
    # Mapear os SKUs do fragmento para seus índices no dataset completo
    # SKUs não encontrados no modelo original se tornarão NaN
    fragment_indices_in_full_model = df_fragment['sku'].map(sku_to_full_index)
    
    # Criar um DataFrame temporário com os SKUs e seus índices correspondentes
    available_items = pd.DataFrame({
        'sku': df_fragment['sku'],
        'full_index': fragment_indices_in_full_model
    }).dropna().set_index(pd.RangeIndex(len(df_fragment))).dropna() # Limpa e reindexa

    if len(available_items) != len(df_fragment):
        print(f"   ⚠️  Aviso: {len(df_fragment) - len(available_items)} SKUs do fragmento não foram encontrados no modelo e serão ignorados.", flush=True)

    if available_items.empty:
        print("   ❌ Nenhum SKU do fragmento foi encontrado no modelo. Retornando resultados vazios.", flush=True)
        return pd.DataFrame({'sku': df_fragment['sku'], 'recommendations': [[] for _ in df_fragment['sku']]})

    # Obter os vetores (embeddings) apenas dos itens disponíveis
    available_indices = available_items['full_index'].astype(int).tolist()
    available_embeddings = X_reduced[available_indices]
    
    # Armazenar os SKUs dos itens disponíveis para mapeamento posterior
    available_skus = available_items['sku'].values

    # --- PASSO 2: Treinar um modelo KNN temporário APENAS com os itens disponíveis ---
    # O número de vizinhos deve ser o mínimo entre n_recommendations e o total de itens disponíveis
    k = min(n_recommendations + 1, len(available_embeddings))

    temp_knn = NearestNeighbors(n_neighbors=k, metric='cosine')
    temp_knn.fit(available_embeddings)
    
    print(f"   ✅ Modelo KNN temporário treinado com {len(available_embeddings)} itens.", flush=True)

    # --- PASSO 3: Encontrar os vizinhos mais próximos DENTRO do modelo temporário ---
    # A busca é feita nos próprios embeddings disponíveis
    _, neighbor_indices_in_temp_model = temp_knn.kneighbors(available_embeddings)

    # --- PASSO 4: Mapear os índices do modelo temporário de volta para SKUs ---
    # Descartamos a primeira coluna (o próprio item)
    rec_indices = neighbor_indices_in_temp_model[:, 1:]

    # Usamos os 'rec_indices' para buscar os SKUs no nosso array 'available_skus'
    recommended_skus_matrix = available_skus[rec_indices]
    
    # Criar o DataFrame de resultados para os SKUs processados
    results_df = pd.DataFrame({
        'sku': available_skus,
        'recommendations': list(recommended_skus_matrix)
    })

    # --- PASSO 5: Juntar os resultados com o DataFrame original para manter a ordem e os SKUs faltantes ---
    final_df = df_fragment[['sku']].merge(results_df, on='sku', how='left')
    
    # Preencher SKUs que não tiveram recomendações (seja por falta no modelo ou por serem únicos) com listas vazias
    final_df['recommendations'] = final_df['recommendations'].apply(
        lambda x: [] if isinstance(x, float) and np.isnan(x) else list(x)
    )
    
    print(f"✅ Geração de recomendações filtradas concluída.", flush=True)
    return final_df


def generate_recommendations_for_new_item(new_item_dict, model_components, n_recommendations=3):
    """
    Generates recommendations for a single, new item and returns the
    result in a JSON-friendly dictionary format.
    """
    print(f"🔧 Generating recommendations for new item: {new_item_dict.get('sku', 'N/A')}")

    # --- (The first part of the function for preprocessing the new item is unchanged) ---
    df_full_processed = model_components['df_processed']
    scaler = model_components['scaler']
    label_encoders = model_components['label_encoders']
    tfidf = model_components['tfidf']
    encoder_model = model_components['encoder']
    knn = model_components.get('knn_cosine') or model_components.get('knn') # Handle different key names
    
    feature_info = model_components['feature_names']
    num_feat_names = feature_info['numerical']
    cat_feat_names = feature_info['categorical']
    
    new_item_df = pd.DataFrame([new_item_dict])
    
    # Preprocessing steps remain the same...
    new_item_df['popularity_score'] = (new_item_df['rating'] * np.log1p(new_item_df['reviews'])).fillna(0)
    numerical_data = scaler.transform(new_item_df[num_feat_names])
    new_item_df['category_encoded'] = label_encoders['category'].transform(new_item_df['category'])
    new_item_df['productCondition_encoded'] = label_encoders['productCondition'].transform(new_item_df['productCondition'])
    new_item_df['sellerId_encoded'] = label_encoders['sellerId'].transform(new_item_df['sellerId'])
    new_item_df['isBestSeller_encoded'] = new_item_df['isBestSeller'].astype(int)
    new_item_df['active_encoded'] = new_item_df['active'].astype(int)
    new_item_df['in_stock'] = (new_item_df['stock'] > 0).astype(int)
    new_item_df['price_range'] = pd.cut(new_item_df['price'], bins=[0, 50, 100, 200, 500, float('inf')], labels=[0, 1, 2, 3, 4]).cat.add_categories([5]).fillna(5).astype(int)
    new_item_df['rating_category'] = pd.cut(new_item_df['rating'], bins=[0, 3, 4, 4.5, 5], labels=[0, 1, 2, 3]).cat.add_categories([4]).fillna(4).astype(int)
    categorical_data = new_item_df[cat_feat_names].values
    new_item_df['combined_text'] = new_item_df['title'].fillna('')
    text_data = tfidf.transform(new_item_df['combined_text']).toarray()
    
    X_new = np.concatenate([numerical_data, categorical_data, text_data], axis=1)
    X_new_reduced = encoder_model.predict(X_new)
    
    # --- (The last part of the function is changed to return only SKUs) ---
    distances, indices = knn.kneighbors(X_new_reduced, n_neighbors=n_recommendations)
    rec_indices = indices[0]
    
    # Get the list of recommended SKUs
    recommended_skus = df_full_processed.iloc[rec_indices]['sku'].tolist()
    
    # Create the final result object
    final_result = {
        "sku": new_item_dict.get('sku'),
        "recommendations": recommended_skus
    }
    
    return final_result

def save_recommendations_to_json(data, filepath):
    """
    Saves the recommendation data to a JSON file.

    Args:
        data (list or dict): The recommendation results to save.
        filepath (str): The path to the output JSON file.
    """
    try:
        with open(filepath, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=4)
        print(f"✅ Recommendations successfully saved to {filepath}")
    except Exception as e:
        print(f"❌ Error saving file: {e}")



def get_data_from_mongodb(collection_name: str, query_filter: dict = None) -> list:
    """
    Busca dados do MongoDB e retorna uma lista de dicionários.
    """
    client = None
    try:
        conn_params = {
            'host': MONGO_HOST_INPUT,
            'port': MONGO_PORT,
            'serverSelectionTimeoutMS': 5000 # Timeout de conexão mais rápido
        }
        client = MongoClient(**conn_params)
        db = client[MONGO_DB_NAME_INPUT]
        collection = db[collection_name]

        final_filter = query_filter if query_filter is not None else {}
        # Aumentar o timeout da query no lado do servidor
        cursor = collection.find(final_filter).max_time_ms(120000)
        
        # Retorna a lista de documentos diretamente
        return list(cursor)

    except pymongo.errors.ConnectionFailure as e:
        print(f"ERROR: Connection to MongoDB (input) failed: {str(e)}", flush=True)
        raise  # Lança a exceção para ser tratada pela função que chamou
    except Exception as e:
        print(f"ERROR: An unexpected error occurred during get_data: {str(e)}", flush=True)
        raise # Lança a exceção
    finally:
        if client:
            client.close()

def save_data_to_mongodb(data_list: list, collection_name: str, id_field: str = None,
                         mongo_host: str = MONGO_HOST_OUTPUT,
                         mongo_db_name: str = MONGO_DB_NAME_OUTPUT) -> dict:
    client = None
    try:
        # Keep this initial save attempt message
        print(f"DEBUG SAVE: Attempting to save {len(data_list)} docs to host={mongo_host}, db={mongo_db_name}, collection={collection_name}", flush=True)
        
        conn_params = {
            'host': mongo_host,
            'port': MONGO_PORT
        }
        client = MongoClient(**conn_params)
        db = client[mongo_db_name]
        collection = db[collection_name]

        results = []
        for doc_original in data_list:
            doc_to_save = dict(doc_original)

            if '_id' in doc_to_save and isinstance(doc_to_save['_id'], str):
                try:
                    doc_to_save['_id'] = ObjectId(doc_to_save['_id'])
                except:
                    pass

            if id_field and id_field in doc_to_save:
                filter_query = {id_field: doc_to_save[id_field]}
                
                update_result = collection.update_one(
                    filter_query,
                    {"$set": doc_to_save},
                    upsert=True
                )
                if update_result.upserted_id:
                    results.append(f"Inserido novo documento para SKU {doc_to_save[id_field]}") # Simplified print
                elif update_result.modified_count > 0:
                    results.append(f"Documento para SKU {doc_to_save[id_field]} atualizado.") # Simplified print
                else:
                    results.append(f"Documento para SKU {doc_to_save[id_field]} não modificado.") # Simplified print
            else:
                insert_result = collection.insert_one(doc_to_save)
                results.append(f"Inserido novo documento com _id: {insert_result.inserted_id} (no id_field).")
            
        # Keep this final print of the save operation, without dumping results list.
        print(f"DEBUG SAVE: Completed saving process. Total results: {len(results)}.", flush=True)
        return {"status": "success", "message": "Dados salvos com sucesso.", "details": results}

    except pymongo.errors.ConnectionFailure as e:
        print(f"ERROR SAVE: Connection to output MongoDB failed at {mongo_host}:{MONGO_PORT}/{mongo_db_name}: {str(e)}", flush=True)
        return {"status": "error", "message": f"Connection to output MongoDB failed: {str(e)}"}
    except Exception as e:
        print(f"ERROR SAVE: An unexpected error occurred during save_data: {str(e)}", flush=True)
        return {"status": "error", "message": f"An unexpected error occurred: {str(e)}"}
    finally:
        if client:
            client.close()