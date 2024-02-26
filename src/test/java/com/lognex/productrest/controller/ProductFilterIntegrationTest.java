package com.lognex.productrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.service.ProductRestServiceImpl;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static com.lognex.productrest.controller.ProductControllerIntegrationTest.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductFilterIntegrationTest {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductRestServiceImpl productRestServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    // Возможно это стоит удалить, иначе каждый прогон тестов удалит данные из локальной БД
    @AfterEach
    public void resetDb() {
        repository.deleteAll();
    }


    @Test
    void test_givenProducts_whetGetFilteredByAllParams_thenReturnFilteredProducts() throws Exception {
        Product firstProduct = createTestProduct(UUID.randomUUID(), TEST_CORRECT_NAME, TEST_CORRECT_DESCRIPTION, TEST_CORRECT_PRICE, true);
        Product secondProduct = createTestProduct(
                TEST_PRODUCT_ID,
                "For the Emperor",
                "Just test",
                BigDecimal.valueOf(3333.33),
                true
        );
        Product thirdProduct = createTestProduct(
                TEST_NEW_PRODUCT_ID ,
                "Bar foo",
                "just test",
                BigDecimal.valueOf(4444.44),
                true
        );
        Product fourthProduct = createTestProduct(UUID.randomUUID(), TEST_NEW_CORRECT_NAME, TEST_NEW_CORRECT_DESCRIPTION, TEST_NEW_CORRECT_PRICE, false);

        mockMvc.perform(get("/api/products/product")
                        .param("name", "fo")
                        .param("greaterThanPrice", "2345.67")
                        .param("lessThanPrice", "5432.1")
                        .param("availability", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bar foo"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(4444.44)))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].name").value("For the Emperor"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[1].availability").value(true));
    }




    private Product createTestProduct(UUID id, String name, String description, BigDecimal price, boolean availability) {
        Product testProduct = new Product(id, name, description, price, availability);
        return repository.save(testProduct);
    }

}
