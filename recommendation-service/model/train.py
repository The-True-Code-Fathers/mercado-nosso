from recommendation import *


# df_full = pd.read_json(r"C:\Users\Aluno\mercado-nosso-database\bd_completa.json")  # Load the full dataset (1.08 million items)
# train_and_save_model(df_full, "trained_model.pkl")

df_fragment = pd.read_json(r"C:\Users\Aluno\mercado-nosso-database\listing_com_recomendacoes.json")  # Load the 6k-item fragment
model_components = load_model("trained_model.pkl")
recommendations = generate_recommendations_for_fragment(df_fragment, model_components)
print(recommendations)

# new_item = {"sku": "new-sku-123", "name": "New Product", "category": "Category X", "price": 19.99}
# recommendations_for_new_item = generate_recommendations_for_new_item(new_item, df_fragment, model_components)
# print(recommendations_for_new_item)

# import pickle

# # Load the model
# with open("trained_model.pkl", "rb") as f:
#     model_components = pickle.load(f)

# # Print the keys (the names of the items stored inside)
# print(list(model_components.keys()))