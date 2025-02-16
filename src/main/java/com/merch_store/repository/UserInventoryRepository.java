package com.merch_store.repository;

import com.merch_store.repository.dto.UserInventory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserInventoryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createOrUpdate(String username, String inventory, Integer quantity) {
        jdbcTemplate.update(
                "insert into user_inventory (username, inventory_type, quantity) values (?, ?::inventory, ?) " +
                        "on conflict (username, inventory_type) do update " +
                        "set quantity = user_inventory.quantity + ? ",
                username, inventory, quantity, quantity
        );
    }

    public List<UserInventory> findByUserName(String username) {
        return jdbcTemplate.query(
                "select * from user_inventory where username=?",
                new DataClassRowMapper<>(UserInventory.class),
                username
        );
    }
}
