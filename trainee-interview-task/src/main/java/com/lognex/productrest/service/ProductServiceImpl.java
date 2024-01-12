package com.lognex.productrest.service;

import com.lognex.productrest.entity.Product;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Map<UUID, Product> PRODUCT_STORAGE = new HashMap<>();

    @Override
    public void createProduct(Product product) {
        final UUID id = product.getId();
        product.setId(id);
        PRODUCT_STORAGE.put(id, product);
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(PRODUCT_STORAGE.values());
    }

    @Override
    public Product getProductById(UUID id) {
        return PRODUCT_STORAGE.get(id);
    }

    @Override
    public boolean updateProductById(Product product, UUID id) {
        if (PRODUCT_STORAGE.containsKey(id)) {
            product.setId(id);
            PRODUCT_STORAGE.put(id, product);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteProductDyId(UUID id) {
        return PRODUCT_STORAGE.remove(id) != null;
    }
}
