package com.mercadonosso.reviews_service.core.domain.exception;

public class ReviewsNotFoundException extends RuntimeException {
    public ReviewsNotFoundException (String message) {
        super(message);
    }
}
