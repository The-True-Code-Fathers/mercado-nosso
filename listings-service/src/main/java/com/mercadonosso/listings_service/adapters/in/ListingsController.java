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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mercadonosso.listings_service.adapters.in.web.dto.CreatingListingRequest;
import com.mercadonosso.listings_service.adapters.in.web.dto.ListingResponse;
import com.mercadonosso.listings_service.adapters.in.web.dto.PagedListingResponse;
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
        listingsEntity.setProductSku(String.valueOf(request.productId()));
        listingsEntity.setSellerId(String.valueOf(request.sellerId()));
        listingsEntity.setTitle(request.title());
        listingsEntity.setDescription(request.description());
        listingsEntity.setPrice(request.price());
        listingsEntity.setStock(request.stock());
        listingsEntity.setProductCondition(request.productCondition());
        ListingsEntity createdListingsEntity = listingsServicePort.create(listingsEntity);

        return toResponse(createdListingsEntity);
    }

    @GetMapping("/{id}")
    public ListingResponse getListingById(@PathVariable String id) {
        logger.info("GET /{} - Recebida requisição para buscar listing com ID: {}", id, id);

        // Validação do formato do ObjectId
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

    @GetMapping("/search")
    public List<ListingResponse> searchListings(@RequestParam(required = false) String name,
            @RequestParam(required = false) SearchOrdering ordering,
            @RequestParam(required = false) ProductCondition condition,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        logger.info("GET /search - Recebida requisição para buscar listings com nome parcial: {}, condição: {}, "
                + "preço mínimo: {}, preço máximo: {}, ordenação: {}", name, condition, minPrice, maxPrice, ordering);

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

    private ListingResponse toResponse(ListingsEntity listingsEntity) {
        return new ListingResponse(
                listingsEntity.getListingId() != null ? listingsEntity.getListingId().toHexString() : null,
                listingsEntity.getTitle(),
                listingsEntity.getDescription(),
                listingsEntity.getPrice(),
                listingsEntity.getStock(),
                listingsEntity.isActive(),
                listingsEntity.getProductCondition(),
                listingsEntity.getCreatedAt());
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

        logger.info("GET /search/paginated - Recebida requisição para buscar listings paginados com nome: {}, " +
                "condição: {}, preço mínimo: {}, preço máximo: {}, ordenação: {}, página: {}, tamanho: {}", 
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
                pagedResult.isEmpty()
        );
    }
}