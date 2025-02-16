package com.merch_store.service;

import com.merch_store.repository.UserBalanceRepository;
import com.merch_store.repository.UserRepository;
import com.merch_store.repository.dto.User;
import com.merch_store.repository.dto.UserBalance;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserBalanceRepository userBalanceRepository;

    public User create(String username, String password) {
        userBalanceRepository.create(username);
        return userRepository.create(username, password);
    }

    public User getUser(String username) {
        return userRepository.findByName(username);
    }

    public UserBalance getUserBalance(String username) {
        return userBalanceRepository.findByName(username);
    }

    public void changeBalance(String username, Long balance) {
        userBalanceRepository.setCoinsByName(balance, username);
    }

    public UserDetailsService userDetailsService() {
        return this::getUser;
    }
}
