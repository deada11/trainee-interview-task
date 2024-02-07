package com.lognex.productrest.controller;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.service.ProductRestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private final ProductRestService productRestService;

    public ProductController(ProductRestService productRestService) {
        this.productRestService = productRestService;
    }


    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok().body(productRestService.readAllProducts());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") UUID productId) {
        return ResponseEntity.ok().body(productRestService.readProduct(productId));
    }

    @PostMapping("/product")
    public ResponseEntity<Product> creationProduct(@RequestBody @Valid Product product) {
        return ResponseEntity.ok().body(productRestService.createProduct(product));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updatingProduct(@RequestBody @Valid Product product,
                                               @PathVariable("productId") UUID id) throws CustomEntityNotFoundException {
        return ResponseEntity.ok().body(productRestService.updateProduct(product, id));

    }


    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable("productId") UUID productId) throws CustomEntityNotFoundException {
        Product deletingProduct = productRepository.findById(productId).orElseThrow(() -> new CustomEntityNotFoundException(productId));
        productRepository.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}