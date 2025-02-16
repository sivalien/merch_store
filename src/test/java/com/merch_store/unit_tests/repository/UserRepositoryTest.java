package com.merch_store.unit_tests.repository;

import com.merch_store.repository.UserRepository;
import com.merch_store.repository.dto.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private UserRepository userRepository;

    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        String username = "User";
        String password = "password";
        User user = new User(username, "password");
        String query = "insert into users (username, password) values (?, ?) returning username, password";

        when(jdbcTemplate.query(
                eq(query),
                any(RowMapper.class),
                eq(username),
                eq(password)
        )).thenReturn(List.of(user));

        User actualUser = userRepository.create(username, password);
        assertEquals(user, actualUser);

        verify(jdbcTemplate).query(
                eq(query),
                any(RowMapper.class),
                eq(username),
                eq(password)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindByName_UserExits() {
        String username = "Existing User";
        User user = new User(username, "password");

        String query = "select * from users where username=?";
        when(jdbcTemplate.query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        )).thenReturn(List.of(user));

        assertEquals(user, userRepository.findByName(username));

        verify(jdbcTemplate).query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindByName_UserNotFound() {
        String username = "Fake User";

        String query = "select * from users where username=?";
        when(jdbcTemplate.query(
                eq(query),
                any(RowMapper.class),
                any(String.class)
        )).thenReturn(List.of());

        assertNull(userRepository.findByName(username));

        verify(jdbcTemplate).query(
                eq(query),
                any(RowMapper.class),
                eq(username)
        );
    }
}
