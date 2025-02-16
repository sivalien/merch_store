package com.merch_store.unit_tests.repository;

import com.merch_store.repository.InventoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryRepositoryTest {
    private final InventoryRepository inventoryRepository = new InventoryRepository();

    @Test
    void testFindPrice() {
        assertEquals(80, inventoryRepository.findPrice("t-shirt"));
        assertEquals(500, inventoryRepository.findPrice("pink-hoody"));
    }

    @Test
    void testFindPrice_InventoryNotFound() {
        assertNull(inventoryRepository.findPrice("non-existent-item"));
        assertNull(inventoryRepository.findPrice(""));
    }

}
