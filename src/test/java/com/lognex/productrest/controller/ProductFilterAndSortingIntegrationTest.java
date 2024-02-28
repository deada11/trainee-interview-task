package com.lognex.productrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.service.ProductRestServiceImpl;
import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static com.lognex.productrest.controller.ProductControllerIntegrationTest.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductFilterAndSortingIntegrationTest {
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

/*
Тесты написаны по кейсам, составленным по методике pairwise testing
Дополнительно добавлены несколько простейших тестов по сортировке и лимиту возвращающихся результатов
 */
    @Test
    @Description("Проверяем, что при вызове фильтра по всем имеющимся параметром, но без лимита и без указания сортировки" +
            "возвращается все отфильтрованные продукты, сортированные по умолчанию по возрастанию цены")
    void test_whetGetFilteredByAllParams_thenReturnFilteredProductsAndDefaultSortedByPriceAsc() throws Exception {
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
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("For the Emperor"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].name").value("Bar foo"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(4444.44)))
                .andExpect(jsonPath("$[1].availability").value(true));
    }

    @Test
    @Description("Проверяем, что при фильтрации по имени и диапазону цен возвращаются все все отфильтрованные продукты, отсоротированные по" +
            "умолчанию по возрастанию цены")
    void test_whenGetFilteredByNameAndPriceRange_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                    .param("name", "er")
                    .param("greaterThanPrice", "0.01")
                    .param("lessThanPrice", "34567.89")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Aaron Springer's book of hatred"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(1000.01)))
                .andExpect(jsonPath("$[1].name").value("Bed, 2-nd floor, testers"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[2].name").value("Ma baker! Be wonderful time!"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(4444.44)))
                .andExpect(jsonPath("$[3].name").value("Spooney-wooney's wooah-booer-boom"))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(34567.89)));
    }

    @Test
    @Description("Проверяем, что при фильтрации по имени, нижней границы цены и наличию возвращаются все все отфильтрованные продукты," +
            "отсортированные по умолчанию по возрастанию цены")
    void test_whenGetFilteredByNameGEPriceAvailability_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("name", "om")
                        .param("greaterThanPrice", "12345.67")
                        .param("availability", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Toosey-poosey, bunnar's-bomb"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(12345.67)))
                .andExpect(jsonPath("$[0].availability").value(false))
                .andExpect(jsonPath("$[1].name").value("Spooney-wooney's wooah-booer-boom"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(34567.89)))
                .andExpect(jsonPath("$[1].availability").value(false))
                .andExpect(jsonPath("$[2].name").value("Lonely train is coming to town?"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(45678.9)))
                .andExpect(jsonPath("$[2].availability").value(false));
    }

    @Test
    @Description("Проверяем, что при фильтрации по имени, верхней границы цены и наличию возвращаются все " +
            "все отфильтрованные продукты, отсотрированные по умолчанию по возрастанию цены")
    void test_whenGetFilteredByNameLEPriceAvailability_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("name", "pri")
                        .param("lessThanPrice", "4444.43")
                        .param("availability", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("Zero price, NDA"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.ZERO.setScale(1)))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].name").value("Aaron Springer's book of hatred"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(1000.01)))
                .andExpect(jsonPath("$[1].availability").value(true))
                .andExpect(jsonPath("$[2].name").value("Bruce Springsteen: the worst windows'"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(2002.99)))
                .andExpect(jsonPath("$[2].availability").value(true));
    }

    @Test
    @Description("Проверяем, что при фильтрации по нижней границе цены и наличию возвращаются все все отфильтрованные продукты," +
            "отсортированные по умолчанию по возрастанию цены")
    void test_whenGetFilteredByGreaterThanPriceAndAvailability_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("greaterThanPrice", "2003")
                        .param("availability", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Bed, 2-nd floor, testers"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[0].availability").value(true))
                .andExpect(jsonPath("$[1].name").value("Ma baker! Be wonderful time!"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(4444.44)))
                .andExpect(jsonPath("$[1].availability").value(true))
                .andExpect(jsonPath("$[2].name").value("!@#$%TEST^&*()_+"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(98765.4349).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[2].availability").value(true))
                .andExpect(jsonPath("$[3].name").value("+_)(*&^TEST%$#@!"))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[3].availability").value(true));
    }

    @Test
    @Description("Проверяем, что при фильтрации по верхней границе цены и наличию возвращаются все все отфильтрованные продукты," +
            "отсортированные по умолчанию по возрастанию цены")
    void test_whenGetFilteredByLessThanPriceAndAvailability_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("lessThanPrice", "567.89")
                        .param("availability", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Zuko is the best juice; 2-nd redaction"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(10).setScale(1)))
                .andExpect(jsonPath("$[0].availability").value(false))
                .andExpect(jsonPath("$[1].name").value("22 values of variables: reality and fiction"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(567.89)))
                .andExpect(jsonPath("$[1].availability").value(false));
    }

    @Test
    @Description("Проверяем, что при фильтрации по диапазону цен возвращаются все все отфильтрованные продукты," +
            "отсортированные по умолчанию по возрастанию цены")
    void test_whenGetFilteredByPriceRange_thenReturnFilteredProductsWithDefaultSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("greaterThanPrice", "567.89")
                        .param("lessThanPrice", "3333.34")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("22 values of variables: reality and fiction"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(567.89)))
                .andExpect(jsonPath("$[1].name").value("Aaron Springer's book of hatred"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(1000.01)))
                .andExpect(jsonPath("$[2].name").value("Bruce Springsteen: the worst windows'"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(2002.99)))
                .andExpect(jsonPath("$[3].name").value("Bed, 2-nd floor, testers"))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(3333.33)));
    }

    @Test
    @Description("Проверяем, что при фильтрации только по имени и сортировкой по возрастанию цены," +
            "возвращается весь список продуктов, остортированный по указанному параметру - по возрастанию цены")
    void test_whenGetFilteredByNameAndSortedByPriceAsc_thenReturnedFilteredProductsSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("name", "ER")
                        .param("sortBy", "priceAsc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.ZERO.setScale(1)))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(1000.01)))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(4444.44)))
                .andExpect(jsonPath("$[4].price").value(BigDecimal.valueOf(34567.89)))
                .andExpect(jsonPath("$[5].price").value(BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    @Description("Проверяем, что при фильтрации только по нижней границе цены и сортировкой по убыванию цены," +
            "возвращается весь список продуктов, остортированный по указанному параметру - по убыванию цены")
    void test_whenGetFilteredByGreaterThanPriceAndSortedByPriceDesc_thenReturnedFilteredProductsSortedByPriceDesc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("greaterThanPrice", "12345.67")
                        .param("sortBy", "priceDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(98765.4349).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(45678.9)))
                .andExpect(jsonPath("$[4].price").value(BigDecimal.valueOf(34567.89)))
                .andExpect(jsonPath("$[5].price").value(BigDecimal.valueOf(12345.67)));
    }

    @Test
    @Description("Проверяем, что при фильтрации только по верхней границе цены и сортировкой по убыванию имени," +
            "возвращается весь список продуктов, отсортированный по указанному параметру - обратный по алфавиту")
    void test_whenGetFilteredByLessThanPriceAndSortedByNameDesc_thenReturnedFilteredProductsSortedByNameDesc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("lessThanPrice", "3333.34")
                        .param("sortBy", "nameDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].name").value("Zuko is the best juice; 2-nd redaction"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.TEN.setScale(1)))
                .andExpect(jsonPath("$[1].name").value("Zero price, NDA"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.ZERO.setScale(1)))
                .andExpect(jsonPath("$[2].name").value("Bruce Springsteen: the worst windows'"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(2002.99)))
                .andExpect(jsonPath("$[3].name").value("Bed, 2-nd floor, testers"))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[4].name").value("Aaron Springer's book of hatred"))
                .andExpect(jsonPath("$[4].price").value(BigDecimal.valueOf(1000.01)))
                .andExpect(jsonPath("$[5].name").value("22 values of variables: reality and fiction"))
                .andExpect(jsonPath("$[5].price").value(BigDecimal.valueOf(567.89)));

    }

    @Test
    @Description("Проверяем, что при фильтрации только по наличию и сортировкой по возрастанию имени," +
            "возвращается весь список продуктов, отсортированный по указанному параметру - прямой по алфавиту")
    void test_whenGetFilteredByAvailabilityAndSortedByNameAsc_thenReturnedFilteredProductsSortedByNameAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("availability", "false")
                        .param("sortBy", "nameAsc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].name").value("22 values of variables: reality and fiction"))
                .andExpect(jsonPath("$[0].availability").value(false))
                .andExpect(jsonPath("$[1].name").value("Lonely train is coming to town?"))
                .andExpect(jsonPath("$[1].availability").value(false))
                .andExpect(jsonPath("$[2].name").value("Spooney-wooney's wooah-booer-boom"))
                .andExpect(jsonPath("$[2].availability").value(false))
                .andExpect(jsonPath("$[3].name").value("Toosey-poosey, bunnar's-bomb"))
                .andExpect(jsonPath("$[3].availability").value(false))
                .andExpect(jsonPath("$[4].name").value("Written by TESTER"))
                .andExpect(jsonPath("$[4].availability").value(false))
                .andExpect(jsonPath("$[5].name").value("Zuko is the best juice; 2-nd redaction"))
                .andExpect(jsonPath("$[5].availability").value(false));
    }

    @Test
    @Description("Проверяем, что фильтрации без параметров, только с указанием лимита возвращаемых значений " +
            "и сортировкой по убыванию цены, возвращается лимитированный список продуктов, отсортированный по указанному параметру")
    void test_whenGetFullListWithLimitAndSortedByPriceDesc_thenReturnedLimitedListOfProductsSortedByPriceDesc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("limit", "4")
                        .param("sortBy", "priceDesc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(98765.4349).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(45678.9)));
    }

    @Test
    @Description("Проверяем, что при фильтрации по части имени, которое есть у трех продуктов, при этом у двух из них цены будут округлены так," +
            "что значения цен примут одинаковые значения, возвращается список продуктов и" +
            "этот список будет отсортирован по умолчанию, по возрастанию цены")
    void test_whenGetFilteredByName_thenReturnedFilteredProductsWithRoundedUpPriceAndSortedByPriceAsc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("name", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Bed, 2-nd floor, testers"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(3333.33)))
                .andExpect(jsonPath("$[1].name").value("!@#$%TEST^&*()_+"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(98765.4349).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[2].name").value("Written by TESTER"))
                .andExpect(jsonPath("$[2].price").value(BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[3].name").value("+_)(*&^TEST%$#@!"))
                .andExpect(jsonPath("$[3].price").value(BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    @Description("Проверяем, что при фильтрации по диапазону цен с равными границами и сортировкой по убыванию цены," +
            "возвращается список из двух продуктов с одинаковым значением цены, отсортированный по убыванию цены")
    void test_whenGetFilteredByPriceRangeWithSameBoundsAndSortedByPriceDesc_thenReturnedTwoProductsWithSamePriceAndSortedByPriceDesc() throws Exception {

        createPackOfTestProducts();

        mockMvc.perform(get("/api/products/product")
                        .param("lessThanPrice", "98765.44")
                        .param("greaterThanPrice", "98765.44")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Written by TESTER"))
                .andExpect(jsonPath("$[0].price").value(BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP)))
                .andExpect(jsonPath("$[1].name").value("+_)(*&^TEST%$#@!"))
                .andExpect(jsonPath("$[1].price").value(BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP)));
    }


    private void createPackOfTestProducts() {
        createTestProduct(UUID.randomUUID(), "Zero price, NDA", TEST_CORRECT_DESCRIPTION, BigDecimal.ZERO, true);
        createTestProduct(UUID.randomUUID(), "Aaron Springer's book of hatred", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(1000.01),true);
        createTestProduct(UUID.randomUUID(), "Bruce Springsteen: the worst windows'", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(2002.99), true);
        createTestProduct(UUID.randomUUID(), "Bed, 2-nd floor, testers", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(3333.33), true);
        createTestProduct(UUID.randomUUID(), "Ma baker! Be wonderful time!", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(4444.44), true);
        createTestProduct(UUID.randomUUID(), "Zuko is the best juice; 2-nd redaction", TEST_CORRECT_DESCRIPTION, BigDecimal.TEN, false);
        createTestProduct(UUID.randomUUID(), "22 values of variables: reality and fiction", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(567.89), false);
        createTestProduct(UUID.randomUUID(), "Toosey-poosey, bunnar's-bomb",TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(12345.67), false);
        createTestProduct(UUID.randomUUID(), "Spooney-wooney's wooah-booer-boom", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(34567.89), false);
        createTestProduct(UUID.randomUUID(), "Lonely train is coming to town?", TEST_CORRECT_DESCRIPTION, BigDecimal.valueOf(45678.9), false);
        createTestProduct(UUID.randomUUID(), "!@#$%TEST^&*()_+", TEST_CORRECT_DESCRIPTION,
                BigDecimal.valueOf(98765.4349).setScale(2, RoundingMode.HALF_UP), true);
        createTestProduct(UUID.randomUUID(), "Written by TESTER", TEST_CORRECT_DESCRIPTION,
                BigDecimal.valueOf(98765.4350).setScale(2, RoundingMode.HALF_UP), false);
        createTestProduct(UUID.randomUUID(), "+_)(*&^TEST%$#@!", TEST_CORRECT_DESCRIPTION,
                BigDecimal.valueOf(98765.4351).setScale(2, RoundingMode.HALF_UP), true);
    }

    private Product createTestProduct(UUID id, String name, String description, BigDecimal price, boolean availability) {
        Product testProduct = new Product(id, name, description, price, availability);
        return repository.save(testProduct);
    }

}
