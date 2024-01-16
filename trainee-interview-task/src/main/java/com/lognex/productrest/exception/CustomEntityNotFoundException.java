package com.lognex.productrest.exception;

import java.util.UUID;

public class CustomEntityNotFoundException extends RuntimeException{
    public CustomEntityNotFoundException(UUID id) {
        super("Entity not found " + id + " :(");
    }
}
