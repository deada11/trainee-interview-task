package com.lognex.productrest.controller;

import com.lognex.productrest.entity.Product;
import com.lognex.productrest.service.ProductService;
import com.lognex.productrest.service.ProductServiceImpl;
import com.lognex.productrest.validator.ProductValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        ProductValidator validateCreationProduct = new ProductValidator();
        if (validateCreationProduct.productValidator(product)) {
            productService.createProduct(product);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } else {
            logger.warn("Validation failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProducts(){
        final List<Product> products = productService.getAllProducts();

        if (products != null && !products.isEmpty()) {
            return new ResponseEntity<>(products, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") UUID id) {
        final Product product = productService.getProductById(id);

        if (product != null) {
            return new ResponseEntity<>(product, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProductById(@PathVariable("productId") UUID id,
                                                     @RequestBody Product product) {

        ProductValidator validateUpdateProduct = new ProductValidator();
        if (validateUpdateProduct.productValidator(product)) {
            final boolean updated = productService.updateProductById(product, id);
            if (updated) {
                return new ResponseEntity<>(product, HttpStatus.OK);
            } else {
                logger.warn("Product not found");
                return new ResponseEntity<>(product, HttpStatus.BAD_REQUEST);
            }
        } else {
            logger.warn("Product not modified, validation failed");
            return new ResponseEntity<>(product, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProductDyId(@PathVariable("productId") UUID id) {
        final boolean deleted = productService.deleteProductDyId(id);

        if (deleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }
}