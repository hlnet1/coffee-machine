package com.example.coffeemachine.integration;

import com.example.coffeemachine.entity.Product;
import com.example.coffeemachine.enums.CoinEnum;
import com.example.coffeemachine.enums.ItemEnum;
import com.example.coffeemachine.exception.InsufficientAmountException;
import com.example.coffeemachine.state.MachineState;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CoffeeMachineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MachineState machineState;


    private Product testProduct;

    @BeforeEach
    void setUp() {

        machineState.clearInsertedCoins();

        testProduct = new Product();
        testProduct.setProductName(ItemEnum.WATER.getName());
        testProduct.setProductPrice(ItemEnum.WATER.getPrice());
        testProduct.setQuantity(5);
    }

    @Test
    void completeVendingFlow_ShouldWorkSuccessfully() throws Exception {

        MvcResult addResult = mockMvc.perform(post("/api/vending/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        Product addedProduct = objectMapper.readValue(addResult.getResponse().getContentAsString(), Product.class);
        assertNotNull(addedProduct.getId());


        mockMvc.perform(post("/api/vending/coins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CoinEnum.ONE_LV)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/vending/coins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CoinEnum.TWENTY_ST)))
                .andExpect(status().isOk());


        MvcResult totalResult = mockMvc.perform(get("/api/vending/coins/total"))
                .andExpect(status().isOk())
                .andReturn();

        int totalInserted = objectMapper.readValue(totalResult.getResponse().getContentAsString(), Integer.class);
        assertEquals(120, totalInserted);


        MvcResult buyResult = mockMvc.perform(post("/api/vending/products/{productId}/buy", addedProduct.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Map<CoinEnum, Integer> change = objectMapper.readValue(buyResult.getResponse().getContentAsString(), Map.class);
        assertNotNull(change);


        MvcResult productsResult = mockMvc.perform(get("/api/vending/products"))
                .andExpect(status().isOk())
                .andReturn();

        List<Product> products = objectMapper.readValue(productsResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Product.class));

        Product updatedProduct = products.stream()
                .filter(p -> p.getProductName().equals(addedProduct.getProductName()))
                .findFirst()
                .orElseThrow();

        assertEquals(4, updatedProduct.getQuantity());


        MvcResult stateResult = mockMvc.perform(get("/api/vending/state/inserted-coins"))
                .andExpect(status().isOk())
                .andReturn();

        List<CoinEnum> insertedCoins = objectMapper.readValue(stateResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CoinEnum.class));

        assertTrue(insertedCoins.isEmpty());
    }

    @Test
    void returnCoins_ShouldReturnAllInsertedCoins() throws Exception {

        mockMvc.perform(post("/api/vending/coins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CoinEnum.ONE_LV)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/vending/coins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CoinEnum.TWENTY_ST)))
                .andExpect(status().isOk());

        MvcResult returnResult = mockMvc.perform(post("/api/vending/coins/return"))
                .andExpect(status().isOk())
                .andReturn();

        List<CoinEnum> returnedCoins = objectMapper.readValue(returnResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CoinEnum.class));

        assertEquals(2, returnedCoins.size());
        assertEquals(CoinEnum.ONE_LV, returnedCoins.get(0));
        assertEquals(CoinEnum.TWENTY_ST, returnedCoins.get(1));

        MvcResult stateResult = mockMvc.perform(get("/api/vending/state/inserted-coins"))
                .andExpect(status().isOk())
                .andReturn();

        List<CoinEnum> insertedCoins = objectMapper.readValue(stateResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, CoinEnum.class));

        assertTrue(insertedCoins.isEmpty());
    }

    @Test
    void buyProduct_ShouldFail_WhenInsufficientAmount() throws Exception {

        MvcResult addResult = mockMvc.perform(post("/api/vending/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isOk())
                .andReturn();

        Product addedProduct = objectMapper.readValue(addResult.getResponse().getContentAsString(), Product.class);


        mockMvc.perform(post("/api/vending/coins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CoinEnum.TWENTY_ST)))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/vending/products/{productId}/buy", addedProduct.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof InsufficientAmountException))
                .andExpect(result -> assertEquals(
                        "Insufficient amount. Need to provide 30 stotinki more",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void machineState_ShouldInitializeWithChange() throws Exception {
        MvcResult changeResult = mockMvc.perform(get("/api/vending/state/change"))
                .andExpect(status().isOk())
                .andReturn();

        Map<CoinEnum, Integer> availableChange = objectMapper.readValue(changeResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructMapType(Map.class, CoinEnum.class, Integer.class));


        for (CoinEnum coin : CoinEnum.values()) {
            assertEquals(10, availableChange.get(coin));
        }
    }
}
