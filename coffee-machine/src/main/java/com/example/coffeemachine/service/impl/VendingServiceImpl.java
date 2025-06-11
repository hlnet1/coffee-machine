package com.example.coffeemachine.service.impl;

import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;
import com.example.coffeemachine.exception.InsufficientAmountException;
import com.example.coffeemachine.exception.NotEnoughChangeException;
import com.example.coffeemachine.exception.ProductNotFoundException;
import com.example.coffeemachine.exception.ProductOutOfStockException;
import com.example.coffeemachine.repository.ProductRepository;
import com.example.coffeemachine.service.VendingService;
import com.example.coffeemachine.state.MachineState;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class VendingServiceImpl implements VendingService {

    private final ProductRepository productRepository;
    private final MachineState machineState;

    @Override
    @Transactional
    public Product addNewProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        machineState.addProduct(product);
        log.info("New product - {} has been added, price {}, quantity {}", product.getProductName(), product.getProductPrice(), product.getQuantity());
        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        if (product.getProductName() != null) {
            existingProduct.setProductName(product.getProductName());
        }

        if (product.getProductPrice() != null) {
            existingProduct.setProductPrice(product.getProductPrice());
        }

        existingProduct.setQuantity(product.getQuantity());

        Product updatedProduct = productRepository.save(existingProduct);
        machineState.addProduct(updatedProduct);
        log.info("Product with id: {} has been updated successfully. ", id);

        return updatedProduct;
    }

    @Override
    @Transactional
    public void removeProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        machineState.getInventory().remove(product.getProductName());
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Map<CoinEnum, Integer> buyProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));

        if (product.getQuantity() <= 0) {
            throw new ProductOutOfStockException(product.getProductName());
        }

        int totalInserted = machineState.getInsertedCoins().stream()
                .mapToInt(CoinEnum::getDenomination)
                .sum();

        int productPrice = product.getProductPrice();

        if (totalInserted < productPrice) {
            throw new InsufficientAmountException(totalInserted, productPrice);
        }

        int change = totalInserted - productPrice;
        Map<CoinEnum, Integer> changeCoins = calculateChange(change);

        product.setQuantity(product.getQuantity() - 1);
        Product updatedProduct = this.updateProduct(productId,product);

        machineState.clearInsertedCoins();

        return changeCoins;
    }

    @Override
    public void insertCoin(CoinEnum coin) {
        machineState.addInsertedCoin(coin);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<CoinEnum> returnCoins() {
        List<CoinEnum> returnedCoins = new ArrayList<>(machineState.getInsertedCoins());
        machineState.clearInsertedCoins();
        return returnedCoins;
    }

    @Override
    public int getTotalInsertedSum() {
        return machineState.getInsertedCoins().stream()
                .mapToInt(CoinEnum::getDenomination)
                .sum();
    }

    @Override
    public double getCurrentChangeBallance() {
        return machineState.getCurrentBalance();
    }

    private Map<CoinEnum, Integer> calculateChange(int changeInStotinki) {
        Map<CoinEnum, Integer> changeCoins = new HashMap<>();
        Map<CoinEnum, Integer> availableChange = new EnumMap<>(machineState.getAvailableChange());
        CoinEnum[] coins = CoinEnum.values();

        for (int i = coins.length - 1; i >= 0 && changeInStotinki > 0; i--) {
            CoinEnum coin = coins[i];
            int coinValue = coin.getDenomination();
            int available = availableChange.getOrDefault(coin, 0);

            while (changeInStotinki >= coinValue && available > 0) {
                changeInStotinki -= coinValue;
                available--;
                changeCoins.merge(coin, 1, Integer::sum);
            }

            availableChange.put(coin, available);
        }
        if (changeInStotinki > 0) {
            throw new NotEnoughChangeException();
        }
        machineState.setAvailableChange(availableChange);
        return changeCoins;
    }
}
