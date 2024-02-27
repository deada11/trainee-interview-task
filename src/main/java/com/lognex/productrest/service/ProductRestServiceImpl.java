package com.lognex.productrest.service;

import com.lognex.productrest.dao.ProductRepository;
import com.lognex.productrest.entity.Product;
import com.lognex.productrest.exception.CustomEntityNotFoundException;
import com.lognex.productrest.validator.ProductValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductRestServiceImpl implements ProductRestService{

    public ProductRestServiceImpl(){}
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    public ProductRestServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public void createProduct(Product product) {
        ProductValidator productValidator = new ProductValidator();
        productValidator.priceChanger(product);
        productRepository.save(product);
    }

    public List<Product> readAllProducts(String name,
                                         BigDecimal lessThanPrice,
                                         BigDecimal greaterThanPrice,
                                         Boolean availability,
                                         String sortBy,
                                         Integer limit) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);

        // В отдельный метод фильтрацию и сортировку.
        // На вход передавать имя, цену, наличие, сортбай.
        // Возвращает CriteriaQuery
        // в виде criteriaQuery.where(criteria)

        Predicate criteria = criteriaBuilder.conjunction();
        if (name != null) {
            criteria = criteriaBuilder.and(criteria,
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name + "%".toLowerCase()));
        }

        if (greaterThanPrice != null) {
            criteria = criteriaBuilder.and(criteria,
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), greaterThanPrice));
        }

        if (lessThanPrice != null) {
            criteria = criteriaBuilder.and(criteria,
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), lessThanPrice));
        }

        if (availability != null) {
            criteria = criteriaBuilder.and(criteria,
                    criteriaBuilder.equal(root.get("availability"), availability));
        }

        criteriaQuery.where(criteria);

        if (sortBy != null) {
            if (sortBy.equalsIgnoreCase("name")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));
            }
            if (sortBy.equalsIgnoreCase("price")) {
                criteriaQuery.orderBy(criteriaBuilder.asc(root.get("price")));
            }
        } else {
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("price")));
        }

        TypedQuery<Product> query = entityManager.createQuery(criteriaQuery);
        if (limit != null) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
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

    private void createFilterAndSorter(String name,
                                       BigDecimal lessThanPrice,
                                       BigDecimal greaterThanPrice,
                                       Boolean availability,
                                       String sortBy,
                                       Integer limit) {

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
