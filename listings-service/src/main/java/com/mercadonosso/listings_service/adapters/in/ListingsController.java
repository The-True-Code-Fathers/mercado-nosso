package com.mercadonosso.listings_service.adapters.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.listings_service.adapters.in.web.dto.CreatingListingRequest;
import com.mercadonosso.listings_service.adapters.in.web.dto.ListingResponse;
import com.mercadonosso.listings_service.adapters.in.web.dto.PagedListingResponse;
import com.mercadonosso.listings_service.adapters.in.web.dto.UpdateListingsRequest;
import com.mercadonosso.listings_service.core.domain.ListingsEntity;
import com.mercadonosso.listings_service.core.domain.PagedResult;
import com.mercadonosso.listings_service.core.domain.Pagination;
import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import com.mercadonosso.listings_service.core.domain.enums.SearchOrdering;
import com.mercadonosso.listings_service.core.ports.in.ListingsServicePort;

@RestController
public class ListingsController {

    private static final Logger logger = LoggerFactory.getLogger(ListingsController.class);
    private final ListingsServicePort listingsServicePort;

    public ListingsController(ListingsServicePort listingsServicePort) {
        this.listingsServicePort = listingsServicePort;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse createListing(@RequestBody CreatingListingRequest request) {
        ListingsEntity listingsEntity = new ListingsEntity();
        listingsEntity.setSellerId(String.valueOf(request.sellerId()));
        listingsEntity.setSku(request.sku());
        listingsEntity.setProductRecommendation(request.productRecommendation());
        listingsEntity.setTitle(request.title());
        listingsEntity.setDescription(request.description());
        listingsEntity.setPrice(request.price());
        listingsEntity.setRating(request.rating());
        listingsEntity.setReviewsId(request.reviewsId());
        listingsEntity.setImagesUrl(request.imagesUrl());
        listingsEntity.setCategory(request.category());
        listingsEntity.setStock(request.stock());
        listingsEntity.setProductCondition(request.productCondition());
        ListingsEntity createdListingsEntity = listingsServicePort.create(listingsEntity);

        return toResponse(createdListingsEntity);
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable String id) {
        logger.info("GET /{} - Recebida requisição para buscar listing com ID: {}", id, id);

        if (!ObjectId.isValid(id)) {
            logger.error(
                    "GET /{} - ID inválido fornecido. ObjectId deve ter 24 caracteres hexadecimais, mas recebeu: {}",
                    id, id);
            throw new IllegalArgumentException(
                    "ID inválido: " + id + ". ObjectId deve ter 24 caracteres hexadecimais.");
        }

        ObjectId objectId = new ObjectId(id);
        logger.info("GET /{} - ObjectId parseado com sucesso: {}", id, objectId.toHexString());

        try {
            ListingsEntity listingsEntity = listingsServicePort.findById(objectId);
            logger.info("GET /{} - Listing encontrado no banco: {}", id,
                    listingsEntity != null ? listingsEntity.getListingId() : "null");

            if (listingsEntity == null) {
                logger.warn("GET /{} - Listing não encontrado no banco de dados", id);
            }

            ListingResponse response = toResponse(listingsEntity);
            logger.info("GET /{} - Resposta gerada: {}", id, response);
            return response;
        } catch (Exception e) {
            logger.error("GET /{} - Erro ao buscar listing", id, e);
            throw e;
        }
    }

    @GetMapping("/item/{sku}")
public ListingResponse getListingBySku(@PathVariable String sku) {
    logger.info("GET /item/{} - Received request to find listing with SKU: {}", sku, sku);

    try {
        // The service port call is correct! It uses your findBySku repository method.
        ListingsEntity listingsEntity = listingsServicePort.findBySku(sku);

        if (listingsEntity == null) {
            logger.warn("GET /item/{} - Listing not found in the database", sku);
            // Consider returning a 404 Not Found response here
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found");
        }

        ListingResponse response = toResponse(listingsEntity);
        logger.info("GET /item/{} - Response generated: {}", sku, response);
        return response;

    } catch (Exception e) {
        logger.error("GET /item/{} - Error while fetching listing", sku, e);
        // Re-throwing the original exception is fine, or map to a specific HTTP status
        throw e;
    }
}

    @PutMapping("/{id}")
    public ListingResponse updateListing(@PathVariable String id, @RequestBody UpdateListingsRequest request) {
        logger.info("PUT /{} - Recebida requisição para atualizar listing com ID: {}", id, id);
        logger.info("PUT /{} - Dados da requisição: sellerId={}, sku={}, title={}, category={}, price={}, stock={}",
                id, request.sellerId(), request.sku(), request.title(), request.category(), request.price(),
                request.stock());

        try {
            if (!ObjectId.isValid(id)) {
                logger.error(
                        "PUT /{} - ID inválido fornecido. ObjectId deve ter 24 caracteres hexadecimais, mas recebeu: {}",
                        id, id);
                throw new IllegalArgumentException(
                        "ID inválido: " + id + ". ObjectId deve ter 24 caracteres hexadecimais.");
            }

            ObjectId objectId = new ObjectId(id);
            logger.info("PUT /{} - ObjectId parseado com sucesso: {}", id, objectId.toHexString());

            // Log detalhado dos dados antes de criar a entidade
            logger.info("PUT /{} - Validando dados da requisição:", id);
            logger.info("PUT /{} - sellerId type: {}, value: {}", id,
                    request.sellerId() != null ? request.sellerId().getClass().getSimpleName() : "null",
                    request.sellerId());
            logger.info("PUT /{} - title: {}", id, request.title());
            logger.info("PUT /{} - description length: {}", id,
                    request.description() != null ? request.description().length() : "null");
            logger.info("PUT /{} - category: {}", id, request.category());
            logger.info("PUT /{} - price: {}", id, request.price());
            logger.info("PUT /{} - stock: {}", id, request.stock());
            logger.info("PUT /{} - productCondition: {}", id, request.productCondition());

            ListingsEntity updateData = new ListingsEntity();
            updateData.setSellerId(String.valueOf(request.sellerId()));
            updateData.setSku(request.sku());
            updateData.setProductRecommendation(request.productRecommendation());
            updateData.setTitle(request.title());
            updateData.setDescription(request.description());
            updateData.setCategory(request.category());
            updateData.setPrice(request.price());
            updateData.setCategory(request.category());
            updateData.setRating(request.rating());
            updateData.setReviewsId(request.reviewsId());
            updateData.setImagesUrl(request.imagesUrl());
            updateData.setStock(request.stock());
            updateData.setSalesCount(request.salesCount());
            updateData.setProductCondition(request.productCondition());

            logger.info("PUT /{} - Entidade criada com sucesso, chamando service para atualizar", id);
            ListingsEntity updatedListing = listingsServicePort.update(objectId, updateData);

            logger.info("PUT /{} - Listing atualizado com sucesso", id);
            return toResponse(updatedListing);

        } catch (IllegalArgumentException e) {
            logger.error("PUT /{} - Erro de validação: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("PUT /{} - Erro inesperado ao atualizar listing: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/search")
    public List<ListingResponse> searchListings(@RequestParam(required = false) String name,
            @RequestParam(required = false) SearchOrdering ordering,
            @RequestParam(required = false) ProductCondition condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        logger.info("GET /search - Searching listings: name={}, condition={}, minPrice={}, maxPrice={}, ordering={}",
                name, condition, minPrice, maxPrice, ordering);

        return listingsServicePort.searchListings(name, condition, minPrice, maxPrice, ordering).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping
    public List<ListingResponse> getAllListings() {
        List<ListingsEntity> listingsEntities = listingsServicePort.listAll();
        return listingsEntities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListingById(@PathVariable String id) {
        logger.info("DELETE /{} - Recebida requisição para deletar listing com ID: {}", id, id);

        // Validação do formato do ObjectId
        if (!ObjectId.isValid(id)) {
            logger.error(
                    "DELETE /{} - ID inválido fornecido. ObjectId deve ter 24 caracteres hexadecimais, mas recebeu: {}",
                    id, id);
            throw new IllegalArgumentException(
                    "ID inválido: " + id + ". ObjectId deve ter 24 caracteres hexadecimais.");
        }

        ObjectId objectId = new ObjectId(id);
        ListingsEntity listingToDelete = listingsServicePort.findById(objectId);
        listingsServicePort.delete(listingToDelete);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/paginated")
    public PagedListingResponse searchListingsPaginated(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) SearchOrdering ordering,
            @RequestParam(required = false) ProductCondition condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        logger.info("GET /search/paginated - Searching listings: name={}, condition={}, minPrice={}, maxPrice={}, ordering={}, page={}, size={}",
                name, condition, minPrice, maxPrice, ordering, page, size);

        Pagination pagination = Pagination.of(page, size);
        PagedResult<ListingsEntity> pagedResult = listingsServicePort.searchListingsPaginated(
                name, condition, minPrice, maxPrice, ordering, pagination);

        List<ListingResponse> listingResponses = pagedResult.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PagedListingResponse(
                listingResponses,
                pagedResult.getPagination().getPage(),
                pagedResult.getPagination().getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.hasNext(),
                pagedResult.hasPrevious(),
                pagedResult.isEmpty());
    }

    private ListingResponse toResponse(ListingsEntity listingsEntity) {
        return new ListingResponse(
                listingsEntity.getListingId() != null ? listingsEntity.getListingId().toHexString() : null,
                listingsEntity.getSellerId(),
                listingsEntity.getSku(),
                listingsEntity.getProductRecommendation(),
                listingsEntity.getTitle(),
                listingsEntity.getDescription(),
                listingsEntity.getPrice(),
                listingsEntity.getStock(),
                listingsEntity.getSalesCount(),
                listingsEntity.getRating(),
                listingsEntity.getReviewsId(),
                listingsEntity.getImagesUrl(),
                listingsEntity.getCategory(),
                listingsEntity.isActive(),
                listingsEntity.getProductCondition());
    }
}
