package com.lognex.productrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class ProductControllerUnitTest {

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

    private final static String TEST_MORE_THAN_255_SYMBOLS_NAME = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium";
    private final static String TEST_MORE_THAN_4096_SYMBOLS_DESCRIPTION = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas in nulla risus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Donec tempus pellentesque elit, nec sodales enim condimentum eget. Nullam eget diam quis diam congue pretium. Nullam finibus mauris non sodales semper. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam tempor eu leo a suscipit. Cras molestie sed diam a convallis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nam ut mauris et ex faucibus tincidunt ut id lacus. Suspendisse quis nisi vitae lorem sagittis convallis non ut ligula. Suspendisse non lacus nisl. Maecenas faucibus scelerisque purus, nec volutpat orci varius vel. Ut ut ullamcorper orci.";
    private final static BigDecimal TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES = BigDecimal.valueOf(444.4444444);
    private final static BigDecimal TEST_NULL_PRICE = null;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductRepository repository;


    @Test
    public void test_givenProduct_whenAdd_thenStatus200andProductReturned() throws Exception {

        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(
                        post("/api/products/product")
                                .content(objectMapper.writeValueAsString(product))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)));
    }

    @Test
    public void test_givenEmptyProduct_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product();
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("Method arguments not valid"));
    }

//    @Test
//    public void test_givenIncorrectProductId_whenAdd_thenThrowsException() throws Exception {}

    @Test
    public void test_givenBigProductName_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID, TEST_MORE_THAN_255_SYMBOLS_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Method arguments not valid"))
                .andExpect(jsonPath("$.errors").value("Product name must be from 1 to 255 characters"));
    }

    @Test
    public void test_givenEmptyName_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                "",
                TEST_CORRECT_DESCRIPTION,
                TEST_NULL_PRICE,
                TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Method arguments not valid"))
                .andExpect(jsonPath("$.errors").value("Product name must be from 1 to 255 characters"));
    }

    @Test
    public void test_givenNullName_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                null,
                TEST_CORRECT_DESCRIPTION,
                TEST_NULL_PRICE,
                TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                        .content(objectMapper.writeValueAsString(product))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Method arguments not valid"))
                .andExpect(jsonPath("$.errors").value("Name cannot be null"));
    }

    @Test
    public void test_givenIncorrectDescription_whenAdd_thenThrowsException() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_MORE_THAN_4096_SYMBOLS_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Method arguments not valid"))
                .andExpect(jsonPath("$.errors").value("Product description cannot be more than 4096 characters"));
    }

    @Test
    public void test_givenIncorrectPriceType_whenAdd_thenStatus200AndPriceCorrect() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_INCORRECT_PRICE_WITH_MORE_THAN_2_DECIMAL_PLACES,
                TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)))
                .andExpect(jsonPath("$.price").value(444.44));
    }

    @Test
    public void test_givenNullPrice_whenAdd_thenStatus200AndPrice0() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID,
                TEST_CORRECT_NAME,
                TEST_CORRECT_DESCRIPTION,
                TEST_NULL_PRICE,
                TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);

        mockMvc.perform(post("/api/products/product")
                .content(objectMapper.writeValueAsString(product))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(product)))
                .andExpect(jsonPath("$.price").value(0));
    }

    @Test
    public void test_givenId_whenGetExistingProduct_thenStatus200andProductReturned() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);

        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(product));

        mockMvc.perform(
                        get("/api/products/product/{id}", TEST_PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId().toString()))
                .andExpect(jsonPath("$.name").value(TEST_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_FALSE));
    }
    @Test
    public void test_givenId_whenGetNotExistingProduct_thenStatus404anExceptionThrown() throws Exception {
        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(
                        get("/api/products/product/{invalid_id}", TEST_NON_EXISTENT_PRODUCT_ID))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(CustomEntityNotFoundException.class));
    }

    @Test
    public void test_giveProduct_whenUpdate_thenStatus200andUpdatedReturns() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.save(Mockito.any())).thenReturn(product);
        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(product));
        mockMvc.perform(
                        put("/api/products/product/{id}", TEST_PRODUCT_ID)
                                .content(objectMapper.writeValueAsString(new Product(TEST_PRODUCT_ID,
                                        TEST_NEW_CORRECT_NAME,
                                        TEST_NEW_CORRECT_DESCRIPTION,
                                        TEST_NEW_CORRECT_PRICE,
                                        TEST_AVAILABILITY_TRUE)))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId().toString()))
                .andExpect(jsonPath("$.name").value(TEST_NEW_CORRECT_NAME))
                .andExpect(jsonPath("$.description").value(TEST_NEW_CORRECT_DESCRIPTION))
                .andExpect(jsonPath("$.price").value(TEST_NEW_CORRECT_PRICE))
                .andExpect(jsonPath("$.availability").value(TEST_AVAILABILITY_TRUE));
    }

    @Test
    public void test_givenProduct_whenDeleteProduct_thenStatus200() throws Exception {
        Product product = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.of(product));
        mockMvc.perform(
                        delete("/api/products/product/{id}", TEST_PRODUCT_ID))
                .andExpect(status().isOk());

    }

    @Test
    public void test_givenProducts_whenGetProducts_thenStatus200() throws Exception {
        Product firstProduct = new Product(TEST_PRODUCT_ID, TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, TEST_AVAILABILITY_TRUE);
        Product secondProduct = new Product(TEST_NEW_PRODUCT_ID, TEST_NEW_CORRECT_NAME, TEST_NEW_CORRECT_DESCRIPTION, TEST_NEW_CORRECT_PRICE, TEST_AVAILABILITY_FALSE);
        Mockito.when(repository.findAll()).thenReturn(Arrays.asList(firstProduct, secondProduct));
        mockMvc.perform(
                        get("/api/products/product"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(firstProduct, secondProduct))));
        ;
    }
}
