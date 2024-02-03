package com.lognex.productrest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
2. Получение продукта по айдишнику:
    передать существующий айдишник - ответ и тело корректные (+)
    передать несуществующий айдишник - ответ, тело и ошибка корректные
    передать корявый айдишник - ответ, тело и ошибка корректные
3. Получение списка продуктов - ответ и тело ответа корретные
4. Изменение продукта по айдишнику:
    передать существующий айдишник, поменять различные поля (имя, цену, наличие, описание и их комбинации) - ответ и тело корректные
    передать несуществующий айдишник, попробовать что-нибудь поменять - ответ, тело и ошибка корректные
    передать корявый айдишник, попробовать что-нибудь поменять - ответ, тело и ошибка корректные
5. Удаление продукта по айдишнику
    передать существующий айдишник - ответ и тело корректные (+)
    передать несуществующий айдишник - ответ, тело и ошибка корректные
    передать корявый айдишник - ответ, тело и ошибка корректные

 */

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    private final static UUID TEST_PRODUCT_ID = UUID.fromString("a935b53c-61b9-45d0-be81-27b10f1dca6e");
    private final static UUID TEST_NON_EXISTENT_PRODUCT_ID = UUID.fromString("1040f406-97b8-490b-bd25-7168abcacfa0");
    private final static UUID TEST_NEW_PRODUCT_ID = UUID.fromString("31dedd55-6175-46ed-ad09-cbcbce65d7a8");
    private final static String TEST_CORRECT_NAME = "test product";
    private final static String TEST_NEW_CORRECT_NAME = "updated test product";
    private final static String TEST_CORRECT_DESCRIPTION = "test description";
    private final static String TEST_NEW_CORRECT_DESCRIPTION = "updated test description";
    private final static BigDecimal TEST_CORRECT_PRICE = BigDecimal.valueOf(1234.56);
    private final static BigDecimal TEST_NEW_CORRECT_PRICE = BigDecimal.valueOf(6543.21);
    private final static boolean TEST_AVAILABILITY_TRUE = true;
    private final static boolean TEST_AVAILABILITY_FALSE = false;
    private final static BigDecimal TEST_NULL_PRICE = null;
    private static final BigDecimal TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES = BigDecimal.valueOf(666.666666);
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    @Description("Проверяет, что при передаче корректных данных для создания продукта" +
            "возвращается созданный корректный продукт")
    public void test_whenGivenCorrectValues_thenCreateValidProduct_OK() throws Exception {

        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE);

        mockMvc.perform(
                post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product)).contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId().toString()))
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.availability").value(product.isAvailability()));
    }

    @Test
    @Description("Проверяет, что при передаче цены с более чем 2-мя знаками после запятой при создании продукта " +
            "происходит округление и возвращается продукт с ценой с 2-мя знаками после запятой")
    public void test_givenIncorrectPriceType_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES,
                TEST_AVAILABILITY_FALSE);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    @Description("Проверяет, что при передаче цены равной NULL при создании продукта создается продукт с ценой 0")
    public void test_givenNullPrice_whenAdd_thenCreateCorrectProduct() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_NULL_PRICE,
                TEST_AVAILABILITY_FALSE);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)))
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    @Description("Проверяем, что при попытке получить по корректному id существующий продукт, в ответе придет " +
            "корректное тело этого продукта")
    public void test_whenHasCorrectProduct_thenGetThisCorrectProduct_OK() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                get("/api/products/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(repository.findById(id).get().getId())))
                .andExpect(jsonPath("$.name").value(TEST_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(repository.findById(id).get().isAvailability()));
    }

    @Test
    @Description("Проверяем, что если продукта не существует, будет выброшена ошибка 404")
    public void test_whenGetNotExistingProduct_thenNotFoundException_404() throws Exception {

        mockMvc.perform(
                get("/api/products/product/{invalid_id}", TEST_NON_EXISTENT_PRODUCT_ID))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(CustomEntityNotFoundException.class));
    }

    @Test
    @Description("Проверяем, что при попытке изменить существующий продукт, возвращается измененное тело измененного продукта")
    public void test_whenGivenCorrectProduct_andUpdate_thenUpdateProduct_OK() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                put("/api/products/product/{id}", id)
                        .content(objectMapper.writeValueAsString(new Product(TEST_PRODUCT_ID, TEST_NEW_CORRECT_NAME,
                                TEST_NEW_CORRECT_DESCRIPTION, TEST_NEW_CORRECT_PRICE, TEST_AVAILABILITY_FALSE)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(repository.findById(id).get().getId())))
                .andExpect(jsonPath("$.name").value(TEST_NEW_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_NEW_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_NEW_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(repository.findById(id).get().isAvailability()));
    }

    @Test
    @Description("Проверям, что при удалении существующего продукта возвращается ответ 200, а при запросе уже удаленного" +
            "продукта возвращается 404 ошибка")
    public void test_deleteProduct_thenReturnOk_andGetDeetedProduct_thenThrowException_OK() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                delete("/api/products/product/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/products/product/{id}", id))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(CustomEntityNotFoundException.class));
    }

    @Test
    @Description("Проверяем, что при запросе всего списка продуктов этот список возвращается")
    public void test_givenListOfProducts_thenReturnAllProducts_OK() throws Exception {
        Product firstProduct = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE);
        Product secondProduct = createTestProduct(TEST_NEW_PRODUCT_ID, TEST_NEW_CORRECT_NAME, TEST_NEW_CORRECT_DESCRIPTION, TEST_NEW_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);

        mockMvc.perform(
                get("/api/products/product"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(firstProduct, secondProduct))));
    }

    private Product createTestProduct(UUID id, String name, String description, BigDecimal price, boolean availability) {
        Product testProduct = new Product(id, name, description, price, availability);
        return repository.save(testProduct);
    }
}