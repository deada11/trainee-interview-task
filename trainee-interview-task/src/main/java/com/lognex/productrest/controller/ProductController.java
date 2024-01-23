package com.lognex.productrest.controller;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private static final Logger logger = LoggerFactory.getLogger(Product.class);


    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable("productId") UUID productId) {
        Optional<Product> product = productRepository.findById(productId);

        if (!product.isPresent()) {
            throw new CustomEntityNotFoundException(productId);
        }
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(product.get());
    }

    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
        if (product.getPrice() == null) {
            product.setPrice(BigDecimal.ZERO);
        } else {
            product.setPrice(product.getPrice().setScale(2, RoundingMode.HALF_UP));
        }
        Product p = productRepository.save(product);
        return ResponseEntity.ok().body(p);
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<Product> updateProduct(@RequestBody @Valid Product product,
                                               @PathVariable("productId") UUID id) throws CustomEntityNotFoundException {
        Optional<Product> p = productRepository.findById(id);
        if (!p.isPresent())
            throw new CustomEntityNotFoundException(id);
        Product oldProduct = p.get();
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setAvailability(product.isAvailability());
        return ResponseEntity.ok().body(productRepository.save(oldProduct));

    }


    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable("productId") UUID productId) throws CustomEntityNotFoundException {
        Product deletingProduct = productRepository.findById(productId).orElseThrow(() -> new CustomEntityNotFoundException(productId));
        productRepository.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}