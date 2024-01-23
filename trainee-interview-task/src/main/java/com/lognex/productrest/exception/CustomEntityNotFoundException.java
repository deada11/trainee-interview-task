package com.lognex.productrest.exception;

import java.util.UUID;

public class CustomEntityNotFoundException extends RuntimeException{
    public CustomEntityNotFoundException(UUID id) {
        super("Product   not found " + id + " :(");
    }
}
