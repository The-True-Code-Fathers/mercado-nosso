package com.mercadonosso.listings_service.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCondition {
    NEW,
    USED;

    @JsonCreator
    public static ProductCondition fromString(String value) {
        if (value == null) {
            return null;
        }
        // Converte para maiúsculas para aceitar tanto "new"/"NEW" quanto "used"/"USED"
        return ProductCondition.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String getValue() {
        return this.name();
    }
}
