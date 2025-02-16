package com.merch_store.repository;

import com.merch_store.repository.dto.UserBalance;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserBalanceRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${load.testing}")
    private Boolean loadTesting;

    public void create(String username) {
        if (loadTesting) {
            jdbcTemplate.update(
                    "insert into user_balance (username, coins) values (?, ?) ",
                    username, 1000000000L
            );
            return;
        }
        jdbcTemplate.update(
                "insert into user_balance (username) values (?) ",
                username
        );
    }

    public UserBalance findByName(String username) {
        List<UserBalance> res = jdbcTemplate.query(
                "select * from user_balance where username=? for no key update",
                new DataClassRowMapper<>(UserBalance.class),
                username
        );
        return res.size() == 0 ? null : res.get(0);
    }

    public void setCoinsByName(Long coins, String username) {
        jdbcTemplate.update("update user_balance set coins=? where username=?", coins, username);
    }
}
