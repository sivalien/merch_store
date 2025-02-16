package com.merch_store.repository;

import com.merch_store.repository.dto.History;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class HistoryRepository {
    private JdbcTemplate jdbcTemplate;

    public List<History> findByFromUser(String username) {
        return jdbcTemplate.query(
                "select * from history where from_user=?",
                new DataClassRowMapper<>(History.class),
                username
        );
    }

    public List<History> findByToUser(String username) {
        return jdbcTemplate.query(
                "select * from history where to_user=?",
                new DataClassRowMapper<>(History.class),
                username
        );
    }

    public void create(String fromUser, String toUser, Long amount) {
        jdbcTemplate.update(
                "insert into history (from_user, to_user, amount) values(?, ?, ?)",
                fromUser, toUser, amount
        );
    }
}
