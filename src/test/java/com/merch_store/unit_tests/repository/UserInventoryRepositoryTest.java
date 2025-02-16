package com.merch_store.unit_tests.repository;

import com.merch_store.repository.UserInventoryRepository;
import com.merch_store.repository.dto.UserInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserInventoryRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserInventoryRepository userInventoryRepository;

    @Test
    public void createOrUpdate() {
        String username = "user";
        String inventory = "hoody";
        Integer quantity = 1;

        userInventoryRepository.createOrUpdate(username, inventory, quantity);

        verify(jdbcTemplate).update(
                eq("insert into user_inventory (username, inventory_type, quantity) values (?, ?::inventory, ?) " +
                        "on conflict (username, inventory_type) do update " +
                        "set quantity = user_inventory.quantity + ? "),
                eq(username), eq(inventory), eq(quantity), eq(quantity)
        );
    }

    @Test
    public void testFindByUserName() {
        String username = "existingUser";
        List<UserInventory> expected = List.of(new UserInventory(username, "hoody", 10));

        when(jdbcTemplate.query(
                eq("select * from user_inventory where username=?"),
                any(RowMapper.class),
                any(String.class)
        )).thenAnswer(invocation -> {
            if (username.equals(invocation.getArgument(2))) {
                return expected;
            }
            return List.of();
        });

        List<UserInventory> result = userInventoryRepository.findByUserName(username);
        assertEquals(1, result.size());
        assertEquals(expected, result);

        result = userInventoryRepository.findByUserName("Fake user");
        assertEquals(0, result.size());
        assertEquals(List.of(), result);

        verify(jdbcTemplate).query(
                eq("select * from user_inventory where username=?"),
                any(RowMapper.class),
                eq(username)
        );

        verify(jdbcTemplate).query(
                eq("select * from user_inventory where username=?"),
                any(RowMapper.class),
                eq("Fake user")
        );
    }
}
