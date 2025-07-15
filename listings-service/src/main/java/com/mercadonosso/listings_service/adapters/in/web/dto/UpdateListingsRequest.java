package com.mercadonosso.listings_service.adapters.in.web.dto;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;

public record UpdateListingsRequest(
                String sku,
                List<String> productRecommendation,
                @NotBlank(message = "O título do anúncio é obrigatório!") @Size(min = 10, max = 80, message = "O titulo deve conter entre 10 e 80 caracteres.") String title,
                String description,
                String category,

                @NotNull(message = "O preço é obrigatório") @DecimalMin(value = "0.01", message = "O preço deve ser maior que 0") @Field(targetType = FieldType.DECIMAL128) BigDecimal price,

                Integer rating,
                List<ObjectId> reviewsId,
                List<String> imagesUrl,

                @NotNull(message = "O estoque não pode ser nulo") Integer stock,

                @NotNull(message = "A condição do produto é obrigatória") ProductCondition productCondition) {
}
