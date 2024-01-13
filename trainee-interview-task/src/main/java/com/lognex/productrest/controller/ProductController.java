package com.lognex.productrest.controller;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.validator.ProductValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(Product.class);


    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        ProductValidator validateCreationProduct = new ProductValidator();
        if (validateCreationProduct.productValidator(product)) {
            Product validProduct = productRepository.save(product);
            return ResponseEntity.status(201).body(validProduct);
        } else {
            logger.warn("Validation failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProducts()   {
        final List<Product> products = productRepository.findAll();
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") UUID id) throws EntityNotFoundException {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            throw new EntityNotFoundException("id - " + id);
        }
        return ResponseEntity.ok().body(product.get());
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProductById(@PathVariable("productId") UUID id,
                                                     @RequestBody Product product) throws EntityNotFoundException {
        // Сделать так, чтоб при передаче ID новая сущность не создавалась, а изменялась существующая
        ProductValidator validateUpdateProduct = new ProductValidator();

        Optional<Product> changingProduct = productRepository.findById(id);
        if (!changingProduct.isPresent()) {
            logger.error("Product not found " + id);
            throw new EntityNotFoundException("id - " + id);
        }
        return (validateUpdateProduct.productValidator(product))? ResponseEntity.ok().body(productRepository.save(product))
                : (new ResponseEntity<>(product, HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProductDyId(@PathVariable("productId") UUID id) throws EntityNotFoundException {
        Optional<Product> deletingProduct = productRepository.findById(id);
        if (!deletingProduct.isPresent()){
            logger.error("Product " + id + " not found ");
            throw new EntityNotFoundException("id - " + id);
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok().body(deletingProduct.get());
    }
}