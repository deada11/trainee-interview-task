package com.lognex.productrest.service;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.validator.ProductValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductRestServiceImpl implements ProductRestService{

    public ProductRestServiceImpl(){}
    @Autowired
    private ProductRepository productRepository;

    public ProductRestServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void createProduct(Product product) {
        ProductValidator productValidator = new ProductValidator();
        productValidator.priceChanger(product);
        productRepository.save(product);
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

    public void updateProduct(Product product, UUID id) {
        Optional<Product> p = productRepository.findById(id);
        if (!p.isPresent())
            throw new CustomEntityNotFoundException(id);
        Product updatebleProduct = p.get();
        productRepository.save(updater(product, updatebleProduct));
    }

    public void deleteProduct(UUID id) {
        Product deletingProduct = productRepository.findById(id).orElseThrow(() -> new CustomEntityNotFoundException(id));
        productRepository.deleteById(id);
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
