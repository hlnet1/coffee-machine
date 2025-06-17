package com.example.coffeemachine.state;

import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
@Slf4j
public class MachineState {
    private Map<String, Product> inventory = new HashMap<>();
    private List<CoinEnum> insertedCoins = new ArrayList<>();
    private Map<CoinEnum, Integer> availableChange = new EnumMap<>(CoinEnum.class);

    public MachineState() {
        initializeCoinInventory();
    }

    public void addProduct(Product product) {
        inventory.put(product.getProductName(), product);
    }

    public boolean hasProduct(String name) {
        return inventory.containsKey(name) && inventory.get(name).getQuantity() > 0;
    }

    public Map<String, Product> getInventory() {
        return inventory;
    }

    public void addInsertedCoin(CoinEnum coin) {
        insertedCoins.add(coin);
    }

    public List<CoinEnum> getInsertedCoins() {
        return insertedCoins;
    }

    public void clearInsertedCoins() {
        insertedCoins.clear();
    }

    public Map<CoinEnum, Integer> getAvailableChange() {
        return availableChange;
    }

    public void setAvailableChange(Map<CoinEnum, Integer> change) {
        this.availableChange = new EnumMap<>(change);
    }

    public void resetChange() {
        availableChange.clear();
        for (CoinEnum coin : CoinEnum.values()) {
            availableChange.put(coin, 10);
        }
    }

    private void initializeCoinInventory() {
        for (CoinEnum coin : CoinEnum.values()) {
            availableChange.put(coin, 10);
        }
        log.info("Coin inventory has been initialized");
    }
}
