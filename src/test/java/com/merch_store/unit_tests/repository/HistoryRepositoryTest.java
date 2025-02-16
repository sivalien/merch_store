package com.merch_store.unit_tests.repository;

import com.merch_store.repository.HistoryRepository;
import com.merch_store.repository.dto.History;
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
public class HistoryRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private HistoryRepository historyRepository;

    @Test
    public void testCreate() {
        String fromUser = "user1";
        String toUser = "user2";
        Long amount = 100L;

        historyRepository.create(fromUser, toUser, amount);

        verify(jdbcTemplate).update(
                eq("insert into history (from_user, to_user, amount) values(?, ?, ?)"),
                eq(fromUser),
                eq(toUser),
                eq(amount)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByFromUser() {
        String username = "user";
        List<History> expected = List.of(
                new History(1L, username, "user1", 100L),
                new History(2L, username, "user2", 300L)
        );
        String query = "select * from history where from_user=?";

        when(jdbcTemplate.query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(expected);

        List<History> result = historyRepository.findByFromUser(username);
        assertEquals(2, result.size());
        assertEquals(expected, result);

        verify(jdbcTemplate).query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findByToUser() {
        String username = "user";
        List<History> expected = List.of(
                new History(1L, "user1", username, 100L),
                new History(2L, "user2", username, 300L)
        );
        String query = "select * from history where to_user=?";

        when(jdbcTemplate.query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(expected);

        List<History> result = historyRepository.findByToUser(username);
        assertEquals(2, result.size());
        assertEquals(expected, result);

        verify(jdbcTemplate).query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        );
    }
}
