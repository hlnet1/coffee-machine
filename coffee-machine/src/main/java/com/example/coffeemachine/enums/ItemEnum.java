package com.example.coffeemachine.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.coffeemachine.exception.ProductNotFoundException;

import java.util.Arrays;
@AllArgsConstructor
@Getter
public enum ItemEnum {

    MOCCA("Mocca", 165),
    TEA("Tea", 110),
    WATER("Water", 50),
    CAPPUCCINO("Cappuccino", 250),
    HOT_CHOCOLATE("Hot Chocolate", 200),
    COFFEE("Coffee", 235);

    private final String name;
    private final int price;

    public static ItemEnum fromName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(name));
    }
}