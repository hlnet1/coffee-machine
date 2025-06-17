package com.example.coffeemachine.exception;

public class InsufficientAmountException extends RuntimeException{
    public InsufficientAmountException(int insertedAmount, int productPrice) {
        super(String.format("Insufficient amount. Need to provide %d stotinki more",productPrice-insertedAmount));
    }
}
