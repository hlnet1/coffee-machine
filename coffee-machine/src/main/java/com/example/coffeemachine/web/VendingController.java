package com.example.coffeemachine.web;

import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;
import com.example.coffeemachine.service.VendingService;
import com.example.coffeemachine.state.MachineState;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vending")
@Tag(name = "Coffee Machine", description = "Coffee Machine API endpoints")
@RequiredArgsConstructor
public class VendingController {
    private final VendingService vendingService;
    private final MachineState machineState;

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.ok(vendingService.addNewProduct(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        return ResponseEntity.ok(vendingService.updateProduct(id, product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> removeProduct(@PathVariable Long id) {
        vendingService.removeProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(vendingService.getAllProducts());
    }

    @PostMapping("/coins")
    public ResponseEntity<Void> insertCoin(@RequestBody CoinEnum coin) {
        vendingService.insertCoin(coin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/coins/return")
    public ResponseEntity<List<CoinEnum>> returnCoins() {
        return ResponseEntity.ok(vendingService.returnCoins());
    }

    @PostMapping("/products/{productId}/buy")
    public ResponseEntity<Map<CoinEnum, Integer>> buyProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(vendingService.buyProduct(productId));
    }

    @GetMapping("/coins/total")
    public ResponseEntity<Integer> getTotalInserted() {
        return ResponseEntity.ok(vendingService.getTotalInsertedSum());
    }

    @GetMapping("/state/balance")
    public ResponseEntity<Double> getCurrentBalance() {
        return ResponseEntity.ok(machineState.getCurrentBalance());
    }

    @GetMapping("/state/change")
    public ResponseEntity<Map<CoinEnum, Integer>> getAvailableChange() {
        return ResponseEntity.ok(machineState.getAvailableChange());
    }

    @GetMapping("/state/inserted-coins")
    public ResponseEntity<List<CoinEnum>> getInsertedCoins() {
        return ResponseEntity.ok(machineState.getInsertedCoins());
    }
}
