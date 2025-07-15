DATABASE="products-service-mongodb"
MONGO_URI="mongodb://localhost:5437"
JSON_DIR="$HOME/development/jsons/partes_product_json"
COLLECTION="products"

for FILE in "$JSON_DIR"/*.json; do
    mongoimport --uri "$MONGO_URI" \
        --db "$DATABASE" \
        --collection "$COLLECTION" \
        --file "$FILE" \
        --jsonArray
done