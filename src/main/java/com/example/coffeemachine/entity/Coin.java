package com.example.coffeemachine.entity;

import com.example.coffeemachine.enums.CoinEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coins")
@Data
@NoArgsConstructor
public class Coin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Coin type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoinEnum coin;

    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    @Column(nullable = false)
    private Integer quantity;

    public Coin(CoinEnum coin, Integer quantity) {
        this.coin = coin;
        this.quantity = quantity;
    }
}