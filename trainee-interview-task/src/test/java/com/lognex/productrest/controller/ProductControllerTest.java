package com.lognex.productrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
1. Создание продукта - ответ и тело ответа корректные
2. Получение продукта по айдишнику:
    передать существующий айдишник - ответ и тело корректные
    передать несуществующий айдишник - ответ, тело и ошибка корректные
    передать корявый айдишник - ответ, тело и ошибка корректные
3. Получение списка продуктов - ответ и тело ответа корретные
4. Изменение продукта по айдишнику:
    передать существующий айдишник, поменять различные поля (имя, цену, наличие, описание и их комбинации) - ответ и тело корректные
    передать несуществующий айдишник, попробовать что-нибудь поменять - ответ, тело и ошибка корректные
    передать корявый айдишник, попробовать что-нибудь поменять - ответ, тело и ошибка корректные
5. Удаление продукта по айдишнику
    передать существующий айдишник - ответ и тело корректные
    передать несуществующий айдишник - ответ, тело и ошибка корректные
    передать корявый айдишник - ответ, тело и ошибка корректные

 */

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    public final static UUID PRODUCT_ID = UUID.fromString("a935b53c-61b9-45d0-be81-27b10f1dca6e");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_createValidProduct_OK() throws Exception {

        Product product = new Product(PRODUCT_ID, "test_product", "test_description",
                BigDecimal.valueOf(123.45),
                true);

        mockMvc.perform(
                post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product)).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").isNumber())
                .andExpect(jsonPath("$.availability").isBoolean());

    }

}