package core.domain;

import core.domain.enums.ProductCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    private UUID listingId;

    @NotNull(message = "O ID do produto é obrigatório.")
    private UUID productId;

    @NotNull(message = "O ID do vendedor é obrigatório.")
    private UUID sellerId;

    @NotBlank(message = "O título do anúncio é obrigatório!")
    @Size(min = 10, max = 80, message = "O titulo deve conter entre 10 e 80 caracteres.")
    private String title;

    private String description;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private BigDecimal price;

    private boolean active;

    private LocalDateTime creationDate;

    @NotNull(message = "A condição do produto é obrigatória.")
    private ProductCondition productCondition;

    private Integer stock;
}