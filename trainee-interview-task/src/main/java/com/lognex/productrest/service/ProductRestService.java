package com.lognex.productrest.service;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.validator.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductRestService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        ProductValidator productValidator = new ProductValidator();
        productValidator.priceChanger(product);
        return productRepository.save(product);
    }

    public List<Product> readAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().toList();
    }

    public Product readProduct(UUID id) {
        Optional<Product> product = productRepository.findById(id);

        if (!product.isPresent()) {
            throw new CustomEntityNotFoundException(id);
        }
        return product.get();
    }

    public Product updateProduct(Product product, UUID id) {
        Optional<Product> p = productRepository.findById(id);
        if (!p.isPresent())
            throw new CustomEntityNotFoundException(id);
        Product updatebleProduct = p.get();
        return productRepository.save(updater(product, updatebleProduct));
    }

    public boolean deleteProduct(UUID id) {
        return false;
    }

    private Product updater(Product product, Product updatableProduct) {
        ProductValidator productValidator = new ProductValidator();

        updatableProduct.setName(product.getName());
        updatableProduct.setDescription(product.getDescription());
        updatableProduct.setPrice(productValidator.priceChanger(product).getPrice());
        updatableProduct.setAvailability(product.isAvailability());
        return updatableProduct;
    }
}
