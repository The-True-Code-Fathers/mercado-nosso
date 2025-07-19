from recommendation import *


df_full = pd.read_json(r"C:\Users\Aluno\mercado-nosso-database\bd_completa.json")  # Load the full dataset (1.08 million items)
train_and_save_model(df_full, "trained_model.pkl")

df_fragment = pd.read_json(r"C:\Users\Aluno\mercado-nosso-database\listing_com_recomendacoes.json")  # Load the 6k-item fragment
model_components = load_model("trained_model.pkl")
recommendations = generate_recommendations_for_fragment(df_fragment, model_components)
print(recommendations)

save_recommendations_to_json(recommendations, "fragment_recommendations.json")

new_product = {
    "sku": "NEW-SKU-123",
    "title": "A Brand New High Quality Product",
    "price": 99.99,
    "rating": 4.8,
    "reviews": 150,
    "boughtInLastMonth": 75,
    "stock": 200,
    "category": "Electronics",
    "productCondition": "New",
    "sellerId": "SELLER-XYZ",
    "isBestSeller": True,
    "active": True
}

model_components = load_model("trained_model.pkl")
new_product_results = generate_recommendations_for_new_item(new_product, model_components)
save_recommendations_to_json([new_product_results], "new_product_recommendation.json")

# import pickle

# # Load the model
# with open("trained_model.pkl", "rb") as f:
#     model_components = pickle.load(f)

# # Print the keys (the names of the items stored inside)
# print(list(model_components.keys()))