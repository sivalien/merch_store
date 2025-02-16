package com.merch_store.integration_tests;

import com.merch_store.controller.dto.AuthRequest;
import com.merch_store.controller.dto.AuthResponse;
import com.merch_store.controller.dto.SendCoinRequest;
import com.merch_store.repository.HistoryRepository;
import com.merch_store.repository.UserBalanceRepository;
import com.merch_store.repository.UserRepository;
import com.merch_store.repository.dto.History;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendCoinTest extends AbstractIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("delete from user_inventory");
        jdbcTemplate.update("delete from history");
        jdbcTemplate.update("delete from user_balance");
        jdbcTemplate.update("delete from users");
    }

    @Test
    public void test() {
        String username1 = "user1";
        String password1 = "password1";
        String username2 = "user2";
        String password2 = "password2";
        Long amount = 200L;

        userRepository.create(username1, passwordEncoder.encode(password1));
        userRepository.create(username2, passwordEncoder.encode(password2));
        userBalanceRepository.create(username1);
        userBalanceRepository.create(username2);

        Long prevCoins1 = userBalanceRepository.findForUpdate(username1).coins();
        Long prevCoins2 = userBalanceRepository.findForUpdate(username2).coins();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Content-Type", "application/json");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                "/api/auth",
                new HttpEntity<>(new AuthRequest(username1, password1), authHeaders),
                AuthResponse.class
        );

        assertTrue(authResponse.getStatusCode().is2xxSuccessful());

        String token = authResponse.getBody().token();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/sendCoin",
                new HttpEntity<>(new SendCoinRequest(username2, amount), headers),
                String.class
        );

        Long coins1 = userBalanceRepository.findForUpdate(username1).coins();
        Long coins2 = userBalanceRepository.findForUpdate(username2).coins();
        History history = historyRepository.findByFromUser(username1)
                .stream()
                .filter(currHistory -> currHistory.toUser().equals(username2) && currHistory.amount().equals(amount))
                .findFirst()
                .orElse(null);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(history);
        assertEquals(amount, prevCoins1 - coins1);
        assertEquals(amount, coins2 - prevCoins2);
    }
}
