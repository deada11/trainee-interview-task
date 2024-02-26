package com.lognex.productrest.validator;

import com.lognex.productrest.entity.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductValidator {

    public Product priceChanger(Product product) {
        BigDecimal normalizedPrice = normalizePrice(product.getPrice());
        product.setPrice(normalizedPrice);
        return product;
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        if (price == null) {
            return BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_UP);
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}