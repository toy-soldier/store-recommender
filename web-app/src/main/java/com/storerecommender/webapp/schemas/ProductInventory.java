package com.storerecommender.webapp.schemas;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** List of products from the store's inventory. */
@Data
public class ProductInventory {
    private List<ProductInventoryLineItem> inventory = new ArrayList<>();
}
