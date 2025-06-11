package com.example.coffeemachine.validation;

import com.example.coffeemachine.validation.annotation.MaxQuantity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxQuantityValidator implements ConstraintValidator<MaxQuantity, Integer> {
    private static final int MAX_QUANTITY = 10;

    @Override
    public boolean isValid(Integer quantity, ConstraintValidatorContext context) {
        if (quantity == null) {
            return true; // Let @NotNull handle null validation
        }
        return quantity <= MAX_QUANTITY;
    }
}

