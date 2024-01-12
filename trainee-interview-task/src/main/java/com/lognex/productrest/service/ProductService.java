package com.lognex.productrest.service;

import com.lognex.productrest.entity.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    void createProduct(Product product);

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    boolean updateProductById(Product product, UUID id);

    boolean deleteProductDyId(UUID id);
}
