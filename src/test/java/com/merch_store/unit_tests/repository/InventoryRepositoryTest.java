package com.merch_store.unit_tests.repository;

import com.merch_store.repository.InventoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryRepositoryTest {
    private final InventoryRepository inventoryRepository = new InventoryRepository();

    @Test
    void testHasMerge() {
        assertTrue(inventoryRepository.hasMerge("t-shirt"));
        assertTrue(inventoryRepository.hasMerge("hoody"));
        assertFalse(inventoryRepository.hasMerge("non-existent-item"));
        assertFalse(inventoryRepository.hasMerge(""));
    }

    @Test
    void testFindPrice_WhenInventoryExists() {
        assertEquals(80, inventoryRepository.findPrice("t-shirt"));
        assertEquals(500, inventoryRepository.findPrice("pink-hoody"));
        assertNull(inventoryRepository.findPrice("non-existent-item"));
        assertNull(inventoryRepository.findPrice(""));
    }

}
