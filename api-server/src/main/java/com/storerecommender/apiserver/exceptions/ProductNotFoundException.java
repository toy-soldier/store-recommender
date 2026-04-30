package com.storerecommender.apiserver.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final Integer sku;

    public ProductNotFoundException(Integer sku) {
        super("Product " + sku + " not found");
        this.sku = sku;
    }
}
