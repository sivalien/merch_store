package com.merch_store.integration_tests;

import com.merch_store.controller.dto.AuthRequest;
import com.merch_store.controller.dto.AuthResponse;
import com.merch_store.repository.UserBalanceRepository;
import com.merch_store.repository.UserInventoryRepository;
import com.merch_store.repository.UserRepository;
import com.merch_store.repository.dto.UserInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuyItemTest extends AbstractIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserInventoryRepository userInventoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("delete from user_inventory");
        jdbcTemplate.update("delete from history");
        jdbcTemplate.update("delete from user_balance");
        jdbcTemplate.update("delete from users");
    }

    private Integer getQuantityByUserAndItem(String username, String item) {
        return userInventoryRepository.findByUserName(username)
                .stream()
                .filter(userInv -> userInv.inventoryType().equals(item))
                .map(UserInventory::quantity)
                .findFirst()
                .orElse(0);
    }

    @Test
    public void test_success() {
        String username = "user1";
        String item = "umbrella";
        String password = "password1";
        Long itemPrice = 200L;

        userRepository.create(username, passwordEncoder.encode(password));
        userBalanceRepository.create(username);
        Integer prevQuantity = getQuantityByUserAndItem(username, item);
        Long prevCoins = userBalanceRepository.findByName(username).coins();

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Content-Type", "application/json");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                "/api/auth",
                new HttpEntity<>(new AuthRequest(username, password), authHeaders),
                AuthResponse.class
        );

        assertTrue(authResponse.getStatusCode().is2xxSuccessful());

        String token = authResponse.getBody().token();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/buy/" + item,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        Integer quantity = getQuantityByUserAndItem(username, item);
        Long coins = userBalanceRepository.findByName(username).coins();

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(1, quantity - prevQuantity);
        assertEquals(itemPrice, prevCoins - coins);
    }
}
