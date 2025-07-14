DATABASE="listings-service-mongodb"
MONGO_URI="mongodb://localhost:5435"
JSON_DIR="$HOME/development/jsons/partes_listing_json"
COLLECTION="listings"

for FILE in "$JSON_DIR"/*.json; do
    mongoimport --uri "$MONGO_URI" \
        --db "$DATABASE" \
        --collection "$COLLECTION" \
        --file "$FILE" \
        --jsonArray
done