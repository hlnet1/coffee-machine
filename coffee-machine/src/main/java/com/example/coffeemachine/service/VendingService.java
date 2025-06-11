package com.example.coffeemachine.service;
import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;

import java.util.List;
import java.util.Map;

public interface VendingService {
    Product addNewProduct(Product product);
    Product updateProduct(Long id, Product product);
    void removeProduct(Long id);
    Map<CoinEnum, Integer> buyProduct(Long productId);
    void insertCoin(CoinEnum coin);
    List<Product> getAllProducts();
    List<CoinEnum> returnCoins();
    int getTotalInsertedSum();
    double getCurrentChangeBallance();
}
