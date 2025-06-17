package com.example.coffeemachine.exception;

public class ProductOutOfStockException extends RuntimeException{
    public ProductOutOfStockException(String productName) {
        super(String.format("Product %s is unavailable at the moment",productName));
    }
}
