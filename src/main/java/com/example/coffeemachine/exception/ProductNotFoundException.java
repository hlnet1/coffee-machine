package com.example.coffeemachine.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String productName) {
        super(String.format("Product %s not found",productName));
    }
}
