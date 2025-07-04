package com.mercadonosso.reviews_service.adapters.in.web.dto;

import jakarta.validation.constraints.*;

public record UpdateReviewsRequest(
        @NotNull(message = "A nota (rating) é obrigatória.")
        @Min(value = 1, message = "A nota mínima é 1.")
        @Max(value = 5, message = "A nota máxima é 5.")
        Integer rating,

        String message
) {}