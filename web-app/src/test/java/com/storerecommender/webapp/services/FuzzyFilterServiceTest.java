package com.storerecommender.webapp.services;

import com.storerecommender.webapp.schemas.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FuzzyFilterServiceTest {
    private final FuzzyFilterService fuzzyFilterService = new FuzzyFilterService(3, 80);

    private final ProductInventory productInventory = new ProductInventory();
    private final ParsedGroceryList parsedGroceryList = new ParsedGroceryList();
    private final ProductInventoryLineItem p1 = new ProductInventoryLineItem(1, "Coca Cola");
    private final ProductInventoryLineItem p2 = new ProductInventoryLineItem(2, "Purefoods hotdog");
    private final ProductInventoryLineItem p3 = new ProductInventoryLineItem(3, "Pepsi cola");
    private final ProductInventoryLineItem p4 = new ProductInventoryLineItem(4, "CDO cheesedog");
    private final ProductInventoryLineItem p5 = new ProductInventoryLineItem(5, "Magnolia butter");
    private final ProductInventoryLineItem p6 = new ProductInventoryLineItem(6, "Zesto cola");
    private final ProductInventoryLineItem p7 = new ProductInventoryLineItem(7, "Alaska milk");
    private final ProductInventoryLineItem p8 = new ProductInventoryLineItem(8, "Nescafe coffee");
    private final ProductInventoryLineItem p9 = new ProductInventoryLineItem(9, "Argentina corned beef");
    private final ProductInventoryLineItem p10 = new ProductInventoryLineItem(10, "Magnolia milk");

    @BeforeEach
    void setUp() {
        productInventory.getInventory().addAll(List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
        parsedGroceryList.getList().addAll(List.of(
                new ParsedGroceryListLineItem("cola", "cola", 1.0, null),
                new ParsedGroceryListLineItem("pizza", "pizza", 1.0, null),
                new ParsedGroceryListLineItem("3L milk", "milk", 3.0, "L")
        ));
    }

    @Test
    void pruneInventoryGivenInventoryAndGroceryListReturnsPrunedInventory() {
        var inputs = new InputsForFuzzyMatching(productInventory, parsedGroceryList);

        PrunedInventory prunedInventory = fuzzyFilterService.pruneInventory(inputs);
        List<PrunedInventoryBasedOnGroceryListLineItem> candidates = prunedInventory.getCandidates();

        assertEquals(parsedGroceryList.getList().size(), candidates.size());
        assertEquals(Set.of(p1, p3, p6), new HashSet<>(candidates.get(0).pruned().getInventory()));
        assertTrue(candidates.get(1).pruned().getInventory().isEmpty());
        assertEquals(Set.of(p7, p10), new HashSet<>(candidates.get(2).pruned().getInventory()));
    }

    @Test
    void pruneInventoryGivenEmptyInventoryAndGroceryListReturnsEmptyInventoryPerLineItem() {
        productInventory.getInventory().clear();
        var inputs = new InputsForFuzzyMatching(productInventory, parsedGroceryList);

        PrunedInventory prunedInventory = fuzzyFilterService.pruneInventory(inputs);

        assertEquals(parsedGroceryList.getList().size(), prunedInventory.getCandidates().size());
        for (PrunedInventoryBasedOnGroceryListLineItem candidate: prunedInventory.getCandidates())
            assertTrue(candidate.pruned().getInventory().isEmpty());
    }

    @Test
    void pruneInventoryGivenInventoryAndEmptyGroceryListReturnsEmptyPrunedInventory() {
        parsedGroceryList.getList().clear();
        var inputs = new InputsForFuzzyMatching(productInventory, parsedGroceryList);

        PrunedInventory prunedInventory = fuzzyFilterService.pruneInventory(inputs);

        assertEquals(0, prunedInventory.getCandidates().size());
    }
}