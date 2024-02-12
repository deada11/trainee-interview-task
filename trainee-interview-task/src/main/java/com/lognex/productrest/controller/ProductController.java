package com.lognex.productrest.controller;

import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.service.ProductRestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private final ProductRestServiceImpl productRestServiceImpl;

    public ProductController(ProductRestServiceImpl productRestServiceImpl) {
        this.productRestServiceImpl = productRestServiceImpl;
    }


    @GetMapping("/product")
    @Transactional
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok().body(productRestServiceImpl.readAllProducts());
    }

    @GetMapping("/product/{productId}")
    @Transactional
    public ResponseEntity<Product> getProduct(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok().body(productRestServiceImpl.readProduct(productId));
    }

    @PostMapping("/product")
    @Transactional
    public ResponseEntity<Product> creationProduct(@RequestBody @Valid Product product) {
        productRestServiceImpl.createProduct(product);
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/product/{productId}")
    @Transactional
    public ResponseEntity<Product> updatingProduct(@RequestBody @Valid Product product,
                                               @PathVariable("productId") UUID id) throws CustomEntityNotFoundException {
        productRestServiceImpl.updateProduct(product, id);
        return ResponseEntity.ok().body(product);

    }

    @DeleteMapping("/product/{productId}")
    @Transactional
    public ResponseEntity<?> deleteProductById(@PathVariable("productId") UUID productId) throws CustomEntityNotFoundException {
        productRestServiceImpl.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}