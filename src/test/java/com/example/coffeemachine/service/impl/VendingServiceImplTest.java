package com.example.coffeemachine.service.impl;

import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;
import com.example.coffeemachine.enums.ItemEnum;
import com.example.coffeemachine.exception.InsufficientAmountException;
import com.example.coffeemachine.exception.NotEnoughChangeException;
import com.example.coffeemachine.exception.ProductNotFoundException;
import com.example.coffeemachine.exception.ProductOutOfStockException;
import com.example.coffeemachine.service.VendingService;
import com.example.coffeemachine.state.MachineState;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VendingServiceImplTest {
    @Autowired
    private VendingService vendingMachineService;

    @Autowired
    private MachineState machineState;

    @BeforeEach
    void setUp() {
        machineState.clearInsertedCoins();
        machineState.resetChange();
    }

    @Test
    void machineState_ShouldInitializeWithChange() {
        Map<CoinEnum, Integer> availableChange = machineState.getAvailableChange();
        for (CoinEnum coin : CoinEnum.values()) {
            assertEquals( 10, availableChange.get(coin));
        }
    }

    @Test
    void addProduct_ShouldAddProductSuccessfully() {
        Product product = new Product();
        product.setProductName("Cola");
        product.setProductPrice(400);
        product.setQuantity(5);

        Product addedProduct = vendingMachineService.addNewProduct(product);

        assertNotNull(addedProduct.getId());
        assertEquals("Cola", addedProduct.getProductName());
        assertEquals(400, addedProduct.getProductPrice());
        assertEquals(5, addedProduct.getQuantity());
        assertTrue(machineState.hasProduct(addedProduct.getProductName()));
    }

    @Test
    void buyProduct_ShouldReturnChange_WhenEnoughMoneyInserted() {
        Product product = createAndSaveProduct(ItemEnum.WATER);

        vendingMachineService.insertCoin(CoinEnum.ONE_LV);

        Map<CoinEnum, Integer> change = vendingMachineService.buyProduct(product.getId());

        assertEquals(1, change.get(CoinEnum.FIFTY_ST));
        assertEquals(4, vendingMachineService.getAllProducts().get(0).getQuantity());
        assertTrue(machineState.getInsertedCoins().isEmpty());
    }

    @Test
    void buyProduct_ShouldThrowException_WhenInsufficientAmount() {
        Product product = createAndSaveProduct(ItemEnum.COFFEE);

        vendingMachineService.insertCoin(CoinEnum.TWO_LV);
        vendingMachineService.insertCoin(CoinEnum.TWENTY_ST);

        assertEquals(220, vendingMachineService.getTotalInsertedSum());
        assertThrows(InsufficientAmountException.class, () -> vendingMachineService.buyProduct(product.getId()));
    }

    @Test
    @DirtiesContext
    void buyProduct_ShouldThrowException_WhenNotEnoughChange() {

        vendingMachineService.insertCoin(CoinEnum.TWO_LV);
        vendingMachineService.insertCoin(CoinEnum.FIFTY_ST);

        assertEquals(250, vendingMachineService.getTotalInsertedSum());
        assertThrows(NotEnoughChangeException.class, () -> vendingMachineService.buyProduct(createAndSaveProduct(ItemEnum.COFFEE).getId()));
        assertEquals(List.of(CoinEnum.TWO_LV,CoinEnum.FIFTY_ST), machineState.getInsertedCoins());
    }

    @Test
    void removeProduct_ShouldThrowException_WhenOutOfStock() {
        Product product = createAndSaveProduct(ItemEnum.HOT_CHOCOLATE);
        product.setQuantity(0);
        vendingMachineService.insertCoin(CoinEnum.TWO_LV);
        assertThrows(ProductOutOfStockException.class, () -> vendingMachineService.buyProduct(product.getId()));
        assertFalse(machineState.hasProduct(product.getProductName()));
    }

    @Test
    void removeProduct_ShouldThrowException_WhenProductNotFound() {
        vendingMachineService.insertCoin(CoinEnum.TWO_LV);
        assertThrows(ProductNotFoundException.class, () -> vendingMachineService.buyProduct(123L));
    }

    @Test
    void returnCoins_ShouldReturnAllInsertedCoins() {
        vendingMachineService.insertCoin(CoinEnum.ONE_LV);
        vendingMachineService.insertCoin(CoinEnum.TWENTY_ST);

        List<CoinEnum> returnedCoins = vendingMachineService.returnCoins();

        assertEquals(2, returnedCoins.size());
        assertEquals(CoinEnum.ONE_LV, returnedCoins.get(0));
        assertEquals(CoinEnum.TWENTY_ST, returnedCoins.get(1));
        assertEquals(0, vendingMachineService.getTotalInsertedSum());
        assertTrue(machineState.getInsertedCoins().isEmpty());
    }

    @Test
    void updateProduct_ShouldUpdateProductQuantitySuccessfully() {
        Product addedProduct = createAndSaveProduct(ItemEnum.COFFEE);

        Product updatedProduct = new Product();
        updatedProduct.setProductName(ItemEnum.COFFEE.getName());
        updatedProduct.setQuantity(3);

        Product result = vendingMachineService.updateProduct(addedProduct.getId(), updatedProduct);

        assertEquals("Coffee", result.getProductName());
        assertEquals(235, result.getProductPrice());
        assertEquals(3, result.getQuantity());
        assertTrue(machineState.hasProduct(result.getProductName()));
    }

    @Test
    void updateProduct_ShouldUpdateProductNameSuccessfully() {
        Product addedProduct = createAndSaveProduct(ItemEnum.COFFEE);

        Product updatedProduct = new Product();
        updatedProduct.setProductName("Espresso");
        updatedProduct.setQuantity(3);

        Product result = vendingMachineService.updateProduct(addedProduct.getId(), updatedProduct);

        assertEquals("Espresso", result.getProductName());
        assertEquals(235, result.getProductPrice());
        assertEquals(3, result.getQuantity());
        assertTrue(machineState.hasProduct(result.getProductName()));
    }

    @Test
    void updateProduct_ShouldUpdateProductPriceSuccessfully() {
        Product addedProduct = createAndSaveProduct(ItemEnum.COFFEE);

        Product updatedProduct = new Product();
        updatedProduct.setProductPrice(Integer.valueOf(335));

        Product result = vendingMachineService.updateProduct(addedProduct.getId(), updatedProduct);

        assertEquals("Coffee", result.getProductName());
        assertEquals(335, result.getProductPrice());
        assertEquals(0, result.getQuantity());
        assertFalse(machineState.hasProduct(result.getProductName()));
    }

    @Test
    void removeProduct_ShouldRemoveProductSuccessfully() {

        Product addedProduct = createAndSaveProduct(ItemEnum.COFFEE);

        vendingMachineService.removeProduct(addedProduct.getId());

        assertThrows(ProductNotFoundException.class, () -> vendingMachineService.buyProduct(addedProduct.getId()));
        assertFalse(machineState.hasProduct(addedProduct.getProductName()));
    }


    private Product createAndSaveProduct(ItemEnum itemEnum) {
        Product product = new Product();
        product.setProductName(itemEnum.getName());
        product.setProductPrice(itemEnum.getPrice());
        product.setQuantity(5);
        return vendingMachineService.addNewProduct(product);
    }
}