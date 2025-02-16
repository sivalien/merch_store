package com.merch_store.unit_tests.service;

import com.merch_store.repository.UserBalanceRepository;
import com.merch_store.repository.UserRepository;
import com.merch_store.repository.dto.User;
import com.merch_store.repository.dto.UserBalance;
import com.merch_store.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreate() {
        String username = "testUser";
        String password = "testPassword";
        User expectedUser = new User(username, password);

        when(userRepository.create(username, password)).thenReturn(expectedUser);

        User result = userService.create(username, password);
        assertNotNull(result);
        assertEquals(expectedUser, result);

        verify(userRepository).create(username, password);
        verify(userBalanceRepository).create(username);
    }

    @Test
    void testGetUser_UserExists() {
        String username = "user";
        User existingUser = new User(username, "testPassword");

        when(userRepository.findByName(username)).thenReturn(existingUser);

        User result = userService.getUser(username);

        assertNotNull(result);
        assertEquals(existingUser, result);

        verify(userRepository).findByName(username);
    }

    @Test
    void testGetUser_UserNotExists() {
        String username = "unknownUser";

        when(userRepository.findByName(username)).thenReturn(null);

        User result = userService.getUser(username);
        assertNull(result);

        verify(userRepository).findByName(username);
    }

    @Test
    void testGetUserBalance_UserExists() {
        String username = "user";
        UserBalance userBalance = new UserBalance(username, 100L);

        when(userBalanceRepository.findByName(username)).thenReturn(userBalance);

        UserBalance result = userService.getUserBalance(username);

        assertNotNull(result);
        assertEquals(userBalance, result);

        verify(userBalanceRepository).findByName(username);
    }

    @Test
    void testGetUserBalance_UserNotExists() {
        String username = "unknownUser";

        when(userBalanceRepository.findByName(username)).thenReturn(null);

        UserBalance result = userService.getUserBalance(username);
        assertNull(result);

        verify(userBalanceRepository).findByName(username);
    }

    @Test
    void testChangeBalance() {
        String username = "user";
        Long balance = 200L;

        userService.changeBalance(username, balance);

        verify(userBalanceRepository).setCoinsByName(balance, username);
    }

    @Test
    void testUserDetailsService_UserExists() {
        String username = "testUser";
        User expectedUser = new User(username, "testPassword");

        when(userRepository.findByName(username)).thenReturn(expectedUser);

        UserDetailsService userDetailsService = userService.userDetailsService();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());

        verify(userRepository).findByName(username);
    }
}
