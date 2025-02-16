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

    public UserBalance getUserBalanceForUpdate(String username) {
        return userBalanceRepository.findForUpdate(username);
    }

    public UserBalance getUserBalanceForRead(String username) {
        return userBalanceRepository.findForRead(username);
    }

    public UserBalance increaseBalance(String username, Long balance) {
        return userBalanceRepository.increaseCoinsByName(balance, username);
    }

    public void decreaseBalance(String username, Long balance) {
        userBalanceRepository.decreaseCoinsByName(balance, username);
    }

    public UserDetailsService userDetailsService() {
        return this::getUser;
    }
}
