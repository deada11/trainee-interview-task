package com.lognex.productrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.service.ProductRestService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private ProductRestService productRestService;

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
    public void test_givenProduct_whenAdd_thenStatus200andProductReturned() throws Exception {

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
        assertEquals(TEST_PRODUCT_ID, productRestService.readProduct(TEST_PRODUCT_ID).getId());
        assertEquals(TEST_CORRECT_NAME, productRestService.readProduct(TEST_PRODUCT_ID).getName());
        assertEquals(TEST_CORRECT_DESCRIPTION, productRestService.readProduct(TEST_PRODUCT_ID).getDescription());
        assertEquals(TEST_CORRECT_PRICE, productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
        assertEquals(TEST_AVAILABILITY_TRUE, productRestService.readProduct(TEST_PRODUCT_ID).isAvailability());
    }

    @Test
    @Description("Проверяет, что при передаче цены с более чем 2-мя знаками после запятой при создании продукта " +
            "происходит округление и возвращается продукт с ценой с 2-мя знаками после запятой")
    public void test_givenIncorrectPriceType_whenAdd_thenStatus200AndPriceCorrect() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES,
                TEST_AVAILABILITY_FALSE);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(666.67));
        assertEquals(BigDecimal.valueOf(666.67), productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
    }

    @Test
    @Description("Проверяет, что при передаче цены равной NULL при создании продукта создается продукт с ценой 0")
    public void test_givenNullPrice_whenAdd_thenStatus200AndPriceBecomesZero() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_NULL_PRICE,
                TEST_AVAILABILITY_FALSE);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(0.00));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
    }

    @Test
    @Description("Проверяет, что при получении пустого продукта падает ошибка")
    public void test_givenEmptyProduct_whenAdd_thenException() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Method arguments not valid"));
    }

    @Test
    @Description("Проверяем, что при попытке получить по корректному id существующий продукт, в ответе придет " +
            "корректное тело этого продукта")
    public void test_givenId_whenGetExistingProduct_thenStatus200andProductReturned() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                get("/api/products/product/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(String.valueOf(TEST_PRODUCT_ID)))
                .andExpect(jsonPath("$.name").value(TEST_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_TRUE));
    }

    @Test
    @Description("Проверяем, что если продукта не существует, при попытке его получить будет выброшена ошибка 404")
    public void test_givenId_whenGetNotExistingProduct_thenStatus404anExceptionThrown() throws Exception {

        mockMvc.perform(
                get("/api/products/product/{invalid_id}", TEST_NON_EXISTENT_PRODUCT_ID))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(CustomEntityNotFoundException.class));
    }
    @Test
    @Description("Проверяем, что при попытке изменить существующий продукт, возвращается новое тело измененного продукта")
    public void test_giveProduct_whenUpdate_thenStatus200andUpdatedReturns() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                put("/api/products/product/{id}", id)
                        .content(objectMapper.writeValueAsString(new Product(TEST_PRODUCT_ID, TEST_NEW_CORRECT_NAME,
                                TEST_NEW_CORRECT_DESCRIPTION, TEST_NEW_CORRECT_PRICE, TEST_AVAILABILITY_FALSE)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(TEST_NEW_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_NEW_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_NEW_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_FALSE));
        assertEquals(TEST_PRODUCT_ID, productRestService.readProduct(TEST_PRODUCT_ID).getId());
        assertEquals(TEST_NEW_CORRECT_NAME, productRestService.readProduct(TEST_PRODUCT_ID).getName());
        assertEquals(TEST_NEW_CORRECT_DESCRIPTION, productRestService.readProduct(TEST_PRODUCT_ID).getDescription());
        assertEquals(TEST_NEW_CORRECT_PRICE, productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
        assertEquals(TEST_AVAILABILITY_FALSE, productRestService.readProduct(TEST_PRODUCT_ID).isAvailability());
    }

    @Test
    @Description("Проверяем, что при попытке передать для изменения цену с более чем 2 знаками после запятой" +
            ", цена будет округлена, знаков останется 2 и вернется новое тело продукта")
    public void test_whenGivenPriceWithMoreThanTwoDecimals_andUpdate_thenPriceHasTwoDecimalsAndUpdateProduct_OK() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                        put("/api/products/product/{id}", id)
                                .content(objectMapper.writeValueAsString(new Product(TEST_PRODUCT_ID, TEST_NEW_CORRECT_NAME,
                                        TEST_NEW_CORRECT_DESCRIPTION, TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES, TEST_AVAILABILITY_FALSE)))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(TEST_NEW_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_NEW_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(666.67)))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_FALSE));
        assertEquals(TEST_PRODUCT_ID, productRestService.readProduct(TEST_PRODUCT_ID).getId());
        assertEquals(TEST_NEW_CORRECT_NAME, productRestService.readProduct(TEST_PRODUCT_ID).getName());
        assertEquals(TEST_NEW_CORRECT_DESCRIPTION, productRestService.readProduct(TEST_PRODUCT_ID).getDescription());
        assertEquals(BigDecimal.valueOf(666.67), productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
        assertEquals(TEST_AVAILABILITY_FALSE, productRestService.readProduct(TEST_PRODUCT_ID).isAvailability());
    }

    @Test
    @Description("Проверяем, что при попытке изменить цену существующего продукта на NULL, цена изменится на 0 и " +
            "вернется корректное тело продукта")
    public void test_giveProductWithNullPrice_whenUpdate_thenPriceBecomesZeroStatus200AndUpdatedReturns() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                        put("/api/products/product/{id}", id)
                                .content(objectMapper.writeValueAsString(new Product(TEST_PRODUCT_ID, TEST_NEW_CORRECT_NAME,
                                        TEST_NEW_CORRECT_DESCRIPTION, TEST_NULL_PRICE, TEST_AVAILABILITY_FALSE)))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.name").value(TEST_NEW_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_NEW_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(0.00))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_FALSE));
        assertEquals(TEST_PRODUCT_ID, productRestService.readProduct(TEST_PRODUCT_ID).getId());
        assertEquals(TEST_NEW_CORRECT_NAME, productRestService.readProduct(TEST_PRODUCT_ID).getName());
        assertEquals(TEST_NEW_CORRECT_DESCRIPTION, productRestService.readProduct(TEST_PRODUCT_ID).getDescription());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), productRestService.readProduct(TEST_PRODUCT_ID).getPrice());
        assertEquals(TEST_AVAILABILITY_FALSE, productRestService.readProduct(TEST_PRODUCT_ID).isAvailability());
    }

    @Test
    @Description("Проверям, что при удалении существующего продукта возвращается ответ 200, а при запросе уже удаленного" +
            "продукта возвращается 404 ошибка")
    public void test_givenProduct_whenDeleteProduct_thenStatus200() throws Exception {
        UUID id = createTestProduct(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE).getId();

        mockMvc.perform(
                delete("/api/products/product/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/products/product/{id}", id))
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(CustomEntityNotFoundException.class));
    }

    @Test
    @Description("Проверяем, что при запросе всего списка продуктов этот список возвращается")
    public void test_givenProducts_whenGetProducts_thenStatus200() throws Exception {
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