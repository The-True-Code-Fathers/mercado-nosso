package com.mercadonosso.listings_service.core.domain;

import com.mercadonosso.listings_service.core.domain.enums.ProductCondition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingsEntity {
    @Id
    private UUID listingId;

    @NotNull(message = "O ID do produto é obrigatório.")
    @Field("product_id")
    private UUID productId;

    @NotNull(message = "O ID do vendedor é obrigatório.")
    @Field("seller_id")
    private UUID sellerId;

    @NotBlank(message = "O título do anúncio é obrigatório!")
    @Size(min = 10, max = 80, message = "O titulo deve conter entre 10 e 80 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal price;

    private boolean active;

    @Field("created_at")
    private LocalDateTime createdAt;

    @NotNull(message = "A condição do produto é obrigatória.")
    @Field("product_condition")
    private ProductCondition productCondition;

    private Integer stock;
}