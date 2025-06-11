package com.example.coffeemachine.exception;

public class NotEnoughChangeException extends RuntimeException{
    public NotEnoughChangeException() {
        super("Not enough change available");
    }
}
