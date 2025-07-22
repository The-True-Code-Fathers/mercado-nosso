package com.mercadonosso.listings_service.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingsEntity {
    @Id
    private ObjectId id;

    @NotNull(message = "O ID do vendedor é obrigatório.")
    private String sellerId;

    private String sku;

    private List<String> productRecommendation;

    @NotBlank(message = "O título do anúncio é obrigatório!")
    @Size(min = 10, max = 80, message = "O titulo deve conter entre 10 e 80 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "O rating deve ser entre 0 e 5.")
    private Integer rating;

    private List<ObjectId> reviewsId;

    @Size(max = 10, message = "Máximo de 10 imagens permitidas.")
    private List<String> imagesUrl;

    private String category;

    private LocalDateTime updatedAt;

    @NotNull(message = "A condição do produto é obrigatória.")
    private ProductCondition productCondition;

    private Integer stock;

    private Integer salesCount = 0;

    private boolean active;
}