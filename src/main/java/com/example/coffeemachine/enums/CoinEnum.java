package com.example.coffeemachine.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoinEnum {
    TEN_ST(10),
    TWENTY_ST(20),
    FIFTY_ST(50),
    ONE_LV(100),
    TWO_LV(200);

    private final int denomination;
}