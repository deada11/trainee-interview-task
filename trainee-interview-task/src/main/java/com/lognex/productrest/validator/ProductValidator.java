package com.lognex.productrest.validator;

import com.lognex.productrest.entity.Product;

public class ProductValidator {

    public boolean productValidator(Product product) {
        if ((product.getName().length() <= 255 && product.getName() != null) &&
                (product.getDescription().length() <= 4096))
        {
            return true;
        } else {
            return false;
        }
    }
}
