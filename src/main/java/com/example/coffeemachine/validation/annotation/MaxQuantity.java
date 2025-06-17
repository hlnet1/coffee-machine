package com.example.coffeemachine.validation.annotation;

import com.example.coffeemachine.validation.MaxQuantityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MaxQuantityValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxQuantity {
    String message() default "Product quantity cannot exceed 10";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
