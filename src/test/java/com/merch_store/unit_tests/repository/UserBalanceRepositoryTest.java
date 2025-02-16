package com.merch_store.unit_tests.repository;

import com.merch_store.repository.UserBalanceRepository;
import com.merch_store.repository.dto.UserBalance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBalanceRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserBalanceRepository userBalanceRepository;

    @Test
    void testCreate() {
        String username = "testUser";

        userBalanceRepository.create(username);

        verify(jdbcTemplate).update(
                eq("insert into user_balance (username) values (?)"),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindForUpdate() {
        String username = "testUser";
        UserBalance expectedUserBalance = new UserBalance(username, 100L);
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(Collections.singletonList(expectedUserBalance));

        UserBalance result = userBalanceRepository.findForUpdate(username);

        assertNotNull(result);
        assertEquals(expectedUserBalance, result);
        verify(jdbcTemplate).query(
                eq("select * from user_balance where username=? for no key update"),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindForUpdate_NotFound() {
        String username = "nonExistentUser";
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(Collections.emptyList());

        UserBalance result = userBalanceRepository.findForUpdate(username);

        assertNull(result);
        verify(jdbcTemplate).query(
                eq("select * from user_balance where username=? for no key update"),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindForRead() {
        String username = "testUser";
        UserBalance expectedUserBalance = new UserBalance(username, 100L);
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(Collections.singletonList(expectedUserBalance));

        UserBalance result = userBalanceRepository.findForRead(username);

        assertNotNull(result);
        assertEquals(expectedUserBalance, result);
        verify(jdbcTemplate).query(
                eq("select * from user_balance where username=? for share"),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testFindForRead_NotFound() {
        String username = "nonExistentUser";
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(Collections.emptyList());

        UserBalance result = userBalanceRepository.findForRead(username);

        assertNull(result);
        verify(jdbcTemplate).query(
                eq("select * from user_balance where username=? for share"),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIncreaseCoinsByName() {
        String username = "testUser";
        Long coins = 50L;
        UserBalance expectedUserBalance = new UserBalance(username, 150L);
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(coins),
                eq(username)
        )).thenReturn(Collections.singletonList(expectedUserBalance));

        UserBalance result = userBalanceRepository.increaseCoinsByName(coins, username);

        assertNotNull(result);
        assertEquals(expectedUserBalance, result);
        verify(jdbcTemplate).query(
                eq("update user_balance set coins=coins + ? where username=? returning username, coins"),
                any(RowMapper.class),
                eq(coins),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void testIncreaseCoinsByName_NotFound() {
        String username = "nonExistentUser";
        Long coins = 50L;
        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                eq(coins),
                eq(username)
        )).thenReturn(Collections.emptyList());

        UserBalance result = userBalanceRepository.increaseCoinsByName(coins, username);

        assertNull(result);
        verify(jdbcTemplate).query(
                eq("update user_balance set coins=coins + ? where username=? returning username, coins"),
                any(RowMapper.class),
                eq(coins),
                eq(username)
        );
    }

    @Test
    void testDecreaseCoinsByName() {
        String username = "testUser";
        Long coins = 50L;

        userBalanceRepository.decreaseCoinsByName(coins, username);

        verify(jdbcTemplate).update(
                eq("update user_balance set coins=coins - ? where username=?"),
                eq(coins),
                eq(username)
        );
    }
}
