package com.merch_store.repository;

import com.merch_store.repository.dto.UserBalance;
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

    public UserBalance findForUpdate(String username) {
        List<UserBalance> res = jdbcTemplate.query(
                "select * from user_balance where username=? for no key update",
                new DataClassRowMapper<>(UserBalance.class),
                username
        );
        return res.size() == 0 ? null : res.get(0);
    }

    public UserBalance findForRead(String username) {
        List<UserBalance> res = jdbcTemplate.query(
                "select * from user_balance where username=? for share",
                new DataClassRowMapper<>(UserBalance.class),
                username
        );
        return res.size() == 0 ? null : res.get(0);
    }

    public UserBalance increaseCoinsByName(Long coins, String username) {
        List<UserBalance> res = jdbcTemplate.query(
                "update user_balance set coins=coins + ? where username=? returning username, coins",
                new DataClassRowMapper<>(UserBalance.class),
                coins,
                username
        );
        return res.size() == 0 ? null : res.get(0);
    }

    public void decreaseCoinsByName(Long coins, String username) {
        jdbcTemplate.update("update user_balance set coins=coins - ? where username=?", coins, username);
    }
}
