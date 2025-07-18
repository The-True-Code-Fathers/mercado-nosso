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

def preprocess_features(df):
    """
    Preprocessa todas as features disponíveis para o sistema de recomendação
    """
    df_processed = df.copy()
    
    # ==================== FEATURES NUMÉRICAS ====================
    # Features numéricas diretas - muito importantes!
    numerical_features = ['price', 'rating', 'reviews', 'boughtInLastMonth', 
                         'stock']
    
    print("✅ Features numéricas incluídas:", numerical_features)
    
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
    
    print("✅ Features categóricas incluídas:", categorical_features)
    
    # ==================== FEATURES DERIVADAS AVANÇADAS ====================
    derived_features = ['popularity_score']
    print("✅ Features derivadas incluídas:", derived_features)
    
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
    print("🔤 Processando features de texto...")
    
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
    
    print(f"✅ {text_features.shape[1]} features de texto extraídas")
    
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
    autoencoder.fit(X, X, epochs=100, batch_size=64, shuffle=True, verbose=1, validation_split=0.2)
    
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

def get_smart_recommendations(product_index, df, model_components, n_recommendations=3, filters=None, use_category_boost=True):
    """
    Função de recomendação inteligente que retorna SKUs em vez de índices.
    """
    knn_cosine = model_components['knn_cosine']
    knn_euclidean = model_components['knn_euclidean']
    X_reduced = model_components['X_reduced']
    
    # ... (code for combining KNN results is unchanged) ...
    original_product = df.iloc[product_index]
    distances_cos, indices_cos = knn_cosine.kneighbors([X_reduced[product_index]], n_neighbors=50)
    distances_euc, indices_euc = knn_euclidean.kneighbors([X_reduced[product_index]], n_neighbors=50)
    combined_scores = {}
    for i, idx in enumerate(indices_cos[0]):
        if idx != product_index:
            combined_scores[idx] = combined_scores.get(idx, 0) + (1 - distances_cos[0][i]) * 0.6
    for i, idx in enumerate(indices_euc[0]):
        if idx != product_index:
            max_dist = np.max(distances_euc[0])
            normalized_dist = distances_euc[0][i] / max_dist if max_dist > 0 else 0
            combined_scores[idx] = combined_scores.get(idx, 0) + (1 - normalized_dist) * 0.4
    if use_category_boost:
        for idx in combined_scores:
            candidate = df.iloc[idx]
            if candidate['category'] == original_product['category']:
                combined_scores[idx] *= 1.5
    sorted_candidates = sorted(combined_scores.items(), key=lambda x: x[1], reverse=True)
    
    # --- Start of Changed Section ---
    recommendations = [] # This will now store SKUs
    similarities = []
    
    for idx, score in sorted_candidates:
        candidate = df.iloc[idx]
        
        # ... (all filter logic is unchanged) ...
        if candidate['rating'] < 2.0 and candidate['reviews'] < 5:
            continue
        if filters:
            if filters.get('same_category') and candidate['category'] != original_product['category']: continue
            if filters.get('same_condition') and candidate['productCondition'] != original_product['productCondition']: continue
            if filters.get('price_range_factor'):
                price_diff = abs(candidate['price'] - original_product['price'])
                if price_diff > original_product['price'] * filters['price_range_factor']: continue
            if filters.get('min_rating') and candidate['rating'] < filters['min_rating']: continue
            if filters.get('in_stock_only') and candidate['stock'] <= 0: continue
            if filters.get('active_only') and not candidate['active']: continue
        
        recommendations.append(candidate['sku']) # ✅ APPEND SKU INSTEAD OF INDEX
        similarities.append(min(score, 1.0))
        
        if len(recommendations) >= n_recommendations:
            break
            
    # The function now returns a list of SKUs and an array of similarities
    return recommendations, np.array(similarities)

def display_recommendations(product_index, df, recommendations, similarities):
    """
    Exibe as recomendações de forma organizada
    """
    original = df.iloc[product_index]
    
    print("=" * 80)
    print("🎯 PRODUTO ORIGINAL")
    print("=" * 80)
    print(f"Título: {original['title']}")
    print(f"Categoria: {original['category']}")
    print(f"Preço: R$ {original['price']:.2f}")
    print(f"Rating: {original['rating']}/5.0 ({original['reviews']} reviews)")
    print(f"Vendido por: {original['sellerId']}")
    print(f"Best Seller: {'Sim' if original['isBestSeller'] else 'Não'}")
    print(f"Compras último mês: {original['boughtInLastMonth']}")
    
    print("\n" + "=" * 80)
    print("🔥 PRODUTOS RECOMENDADOS")
    print("=" * 80)
    
    for i, (idx, similarity) in enumerate(zip(recommendations, similarities)):
        product = df.iloc[idx]
        print(f"\n{i+1}. {product['title']}")
        print(f"   📂 Categoria: {product['category']}")
        print(f"   💰 Preço: R$ {product['price']:.2f}")
        print(f"   ⭐ Rating: {product['rating']}/5.0 ({product['reviews']} reviews)")
        print(f"   🏪 Vendedor: {product['sellerId']}")
        print(f"   🔥 Best Seller: {'Sim' if product['isBestSeller'] else 'Não'}")
        print(f"   🎯 Similaridade: {similarity:.3f}")

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
    print("🔧 Loading the recommendation model...")
    with open(model_path, "rb") as f:
        model_components = pickle.load(f)
    print("✅ Model loaded successfully")
    return model_components


def generate_recommendations_for_fragment(df_fragment, model_components, n_recommendations=3):
    """
    Correctly generates recommendations for a fragment using the full trained model.
    """
    recommendations_map = {}
    
    # ✅ CRITICAL STEP 1: Get the full, processed DataFrame that the model was trained on.
    df_full_processed = model_components['df_processed'] 
    
    # Iterate through each product in the SMALL fragment
    for _, row in df_fragment.iterrows():
        sku = row["sku"]
        try:
            # Find the index of the product in the FULL dataset
            product_index_in_full_df = df_full_processed[df_full_processed['sku'] == sku].index[0]
            
            # ✅ CRITICAL STEP 2: Call get_smart_recommendations using the FULL DataFrame.
            rec_skus, similarities = get_smart_recommendations(
                product_index_in_full_df, 
                df_full_processed,  # Pass the correct, full DataFrame here
                model_components, 
                n_recommendations=n_recommendations
            )
            
            # Look up the recommended items (by their SKUs) in the FULL DataFrame
            if rec_skus:
                recommended_items = df_full_processed.set_index('sku').loc[rec_skus].reset_index().to_dict(orient="records")
                recommendations_map[sku] = recommended_items
            else:
                recommendations_map[sku] = []

        except (ValueError, IndexError):
            print(f"❌ Could not find or process SKU '{sku}' in the trained model's dataset.")
            recommendations_map[sku] = []
            
    return recommendations_map


def generate_recommendations_for_new_item(new_item_dict, model_components, n_recommendations=5):
    """
    Generates recommendations for a single, new item not in the original dataset.
    """
    print(f"🔧 Generating recommendations for new item: {new_item_dict.get('sku', 'N/A')}")

    # --- 1. Load all necessary components ---
    df_full_processed = model_components['df_processed']
    scaler = model_components['scaler']
    label_encoders = model_components['label_encoders']
    tfidf = model_components['tfidf']
    encoder_model = model_components['encoder']
    knn_cosine = model_components['knn_cosine']
    
    feature_info = model_components['feature_names']
    num_feat_names = feature_info['numerical']
    cat_feat_names = feature_info['categorical']
    
    # --- 2. Preprocess the new item's features ---
    new_item_df = pd.DataFrame([new_item_dict])
    
    # A. Handle numerical and derived features
    new_item_df['popularity_score'] = (new_item_df['rating'] * np.log1p(new_item_df['reviews'])).fillna(0)
    numerical_data = scaler.transform(new_item_df[num_feat_names])

    # B. Handle categorical features using the saved encoders
    new_item_df['category_encoded'] = label_encoders['category'].transform(new_item_df['category'])
    new_item_df['productCondition_encoded'] = label_encoders['productCondition'].transform(new_item_df['productCondition'])
    new_item_df['sellerId_encoded'] = label_encoders['sellerId'].transform(new_item_df['sellerId'])
    new_item_df['isBestSeller_encoded'] = new_item_df['isBestSeller'].astype(int)
    new_item_df['active_encoded'] = new_item_df['active'].astype(int)
    new_item_df['in_stock'] = (new_item_df['stock'] > 0).astype(int)
    new_item_df['price_range'] = pd.cut(new_item_df['price'], bins=[0, 50, 100, 200, 500, float('inf')], labels=[0, 1, 2, 3, 4]).cat.add_categories([5]).fillna(5).astype(int)
    new_item_df['rating_category'] = pd.cut(new_item_df['rating'], bins=[0, 3, 4, 4.5, 5], labels=[0, 1, 2, 3]).cat.add_categories([4]).fillna(4).astype(int)
    categorical_data = new_item_df[cat_feat_names].values

    # C. Handle text features
    new_item_df['combined_text'] = new_item_df['title'].fillna('')
    text_data = tfidf.transform(new_item_df['combined_text']).toarray()
    
    # --- 3. Combine features into a single vector ---
    X_new = np.concatenate([numerical_data, categorical_data, text_data], axis=1)
    
    # --- 4. Get the embedding for the new item ---
    X_new_reduced = encoder_model.predict(X_new)
    
    # --- 5. Find neighbors in the original dataset ---
    distances, indices = knn_cosine.kneighbors(X_new_reduced, n_neighbors=n_recommendations)
    
    # The indices are valid for df_full_processed
    rec_indices = indices[0]
    
    # --- 6. Return the recommended items from the full dataset ---
    recommended_items = df_full_processed.iloc[rec_indices].to_dict(orient="records")
    
    return recommended_items
