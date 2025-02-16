package com.merch_store.repository;

import com.merch_store.repository.dto.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class UserRepository {
    private JdbcTemplate jdbcTemplate;

    public User create(String username, String password) {
        return jdbcTemplate.query(
                "insert into users (username, password) values (?, ?) " +
                        "returning username, password",
                new DataClassRowMapper<>(User.class),
                username, password
        ).get(0);
    }

    public User findByName(String username) {
        List<User> res = jdbcTemplate.query(
                "select * from users where username=?",
                new DataClassRowMapper<>(User.class),
                username
        );
        return res.size() == 0 ? null : res.get(0);
    }
}
