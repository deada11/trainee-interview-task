package com.lognex.productrest.controller;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(Product.class);

//    @GetMapping("/product")
//    public ResponseEntity<List<Product>> getAllProducts()   {
//        final List<Product> products = productRepository.findAll();
//        return ResponseEntity.ok().body(products);
//    }
//
//    @GetMapping("/product/{productId}")
//    public Product getProductById(@PathVariable("productId") UUID id)  {
//        return productRepository.findById(id).orElseThrow(() -> new CustomEntityNotFoundException(id));
//    }

//    @PostMapping("/product")
//    public ResponseEntity<Product> createProduct(@RequestBody @Valid Product product) {
//        ProductValidator validateCreationProduct = new ProductValidator();
//        if (validateCreationProduct.productValidator(product)) {
//            Product validProduct = productRepository.save(product);
//            return ResponseEntity.status(201).body(validProduct);
//        } else {
//            logger.warn("Validation failed");
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }

//    @PutMapping("/product/{productId}")
//    public ResponseEntity<Product> updateProductById(@PathVariable("productId") UUID id,
//                                                     @RequestBody @Valid Product product) throws EntityNotFoundException {
////         Сделать так, чтоб при передаче ID новая сущность не создавалась, а изменялась существующая
//        ProductValidator validateUpdateProduct = new ProductValidator();
//
//        Optional<Product> changingProduct = productRepository.findById(id);
//        if (!changingProduct.isPresent()) {
//            logger.error("Product not found " + id);
//            throw new EntityNotFoundException("Entity with id = " + id + " not found :(");
//        }
//        return (validateUpdateProduct.productValidator(product))? ResponseEntity.ok().body(productRepository.save(product))
//                : (new ResponseEntity<>(product, HttpStatus.BAD_REQUEST));
//    }



    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/product/{productId}")
    public Product getProductById(@PathVariable("productId") UUID productId) {
        return productRepository.findById(productId).orElseThrow(() -> new CustomEntityNotFoundException(productId));
    }
    @PostMapping("/product")
    public Product createProduct(@RequestBody @Valid Product product) {
        return productRepository.save(product);
    }

    @PutMapping("/product/{productId}")
    public Product updateProduct(@RequestBody @Valid Product product, @PathVariable UUID productId) {
        Product oldProduct = productRepository.getReferenceById(productId);
        oldProduct.setName(product.getName());
        oldProduct.setDescription(product.getDescription());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setAvailability(product.isAvailability());
        return productRepository.save(oldProduct);
    }


    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable("productId") UUID productId) throws CustomEntityNotFoundException {
        Product deletingProduct = productRepository.findById(productId).orElseThrow(() -> new CustomEntityNotFoundException(productId));
        productRepository.deleteById(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}