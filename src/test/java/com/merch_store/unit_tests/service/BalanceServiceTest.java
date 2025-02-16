package com.merch_store.unit_tests.service;

import com.merch_store.controller.dto.*;
import com.merch_store.exception.BadRequestException;
import com.merch_store.repository.HistoryRepository;
import com.merch_store.repository.InventoryRepository;
import com.merch_store.repository.UserInventoryRepository;
import com.merch_store.repository.dto.History;
import com.merch_store.repository.dto.UserBalance;
import com.merch_store.repository.dto.UserInventory;
import com.merch_store.service.BalanceService;
import com.merch_store.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private UserInventoryRepository userInventoryRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void testPurchase_Success() {
        String username = "testUser";
        String inventory = "item1";
        int price = 100;
        UserBalance user = new UserBalance(username,  200L);

        when(inventoryRepository.findPrice(inventory.toLowerCase())).thenReturn(price);
        when(userService.getUserBalanceForUpdate(username)).thenReturn(user);

        balanceService.purchase(username, inventory);

        verify(userService).getUserBalanceForUpdate(username);
        verify(inventoryRepository).findPrice(inventory);
        verify(userInventoryRepository).createOrUpdate(username, inventory, 1);
        verify(userService).decreaseBalance(username, (long) price);
    }

    @Test
    void testPurchase_ItemNotFound() {
        String username = "testUser";
        String inventory = "item";

        when(inventoryRepository.findPrice(inventory.toLowerCase())).thenReturn(null);

        assertThrows(BadRequestException.class, () -> balanceService.purchase(username, inventory));

        verify(inventoryRepository).findPrice(inventory);
    }

    @Test
    void testPurchase_InsufficientFunds() {
        String username = "testUser";
        String inventory = "item1";
        int price = 100;
        UserBalance user = new UserBalance(username, 50L);

        when(inventoryRepository.findPrice(inventory.toLowerCase())).thenReturn(price);
        when(userService.getUserBalanceForUpdate(username)).thenReturn(user);

        assertThrows(BadRequestException.class, () -> balanceService.purchase(username, inventory));

        verify(userService).getUserBalanceForUpdate(username);
    }

    @Test
    void testTransfer_Success() {
        String fromUsername = "user1";
        String toUsername = "user2";
        long amount = 50L;
        UserBalance fromUser = new UserBalance(fromUsername, 100L);
        UserBalance toUser = new UserBalance(toUsername, 50L);
        SendCoinRequest request = new SendCoinRequest(toUsername, amount);

        when(userService.getUserBalanceForUpdate(fromUsername)).thenReturn(fromUser);
        when(userService.increaseBalance(toUsername, amount)).thenReturn(toUser);

        balanceService.transfer(fromUsername, request);

        verify(userService).getUserBalanceForUpdate(fromUsername);
        verify(userService).decreaseBalance(fromUsername, amount);
        verify(userService).increaseBalance(toUsername, amount);
        verify(historyRepository).create(fromUsername, toUsername, amount);
    }

    @Test
    void testTransfer_InsufficientFunds() {
        String fromUsername = "user1";
        String toUsername = "user2";
        long amount = 150L;
        UserBalance fromUser = new UserBalance(fromUsername, 100L);
        SendCoinRequest request = new SendCoinRequest(toUsername, amount);

        when(userService.getUserBalanceForUpdate(fromUsername)).thenReturn(fromUser);

        assertThrows(BadRequestException.class, () -> balanceService.transfer(fromUsername, request));

        verify(userService).getUserBalanceForUpdate(fromUsername);
    }

    @Test
    void testTransfer_UserNotFound() {
        String fromUsername = "user1";
        String toUsername = "user2";
        long amount = 50L;
        UserBalance fromUser = new UserBalance(fromUsername, 100L);
        SendCoinRequest request = new SendCoinRequest(toUsername, amount);

        when(userService.getUserBalanceForUpdate(fromUsername)).thenReturn(fromUser);
        when(userService.increaseBalance(toUsername, amount)).thenReturn(null);

        assertThrows(BadRequestException.class, () -> balanceService.transfer(fromUsername, request));

        verify(userService).getUserBalanceForUpdate(fromUsername);
        verify(userService).increaseBalance(toUsername, amount);
    }

    @Test
    void testGetUserInfo_Success() {
        String username = "user1";
        String username2 = "user2";
        String username3 = "user3";
        String username4 = "user4";
        Long coins = 100L;
        UserBalance user = new UserBalance(username, coins);
        String inventory = "item1";
        int quantity = 2;

        List<UserInventory> purchases = List.of(new UserInventory(username, inventory, quantity));
        List<History> fromUserTransfers = List.of(
                new History(1L, username, username2, 50L)
        );
        List<History> toUserTransfers = List.of(
                new History(2L, username3, username, 30L),
                new History(2L, username4, username, 100L)
        );

        when(userService.getUserBalanceForRead(username)).thenReturn(user);
        when(userInventoryRepository.findByUserName(username)).thenReturn(purchases);
        when(historyRepository.findByFromUser(username)).thenReturn(fromUserTransfers);
        when(historyRepository.findByToUser(username)).thenReturn(toUserTransfers);

        InfoResponse expected = new InfoResponse(
                coins,
                List.of(new InventoryEntry(inventory, quantity)),
                new CoinHistoryResponse(
                        List.of(
                                new ReceivedEntry(username3, 30L),
                                new ReceivedEntry(username4, 100L)
                        ),
                        List.of(new SentEntry(username2, 50L))
                )
        );
        InfoResponse response = balanceService.getUserInfo(username);

        assertEquals(expected, response);

        verify(userService).getUserBalanceForRead(username);
        verify(userInventoryRepository).findByUserName(username);
        verify(historyRepository).findByFromUser(username);
        verify(historyRepository).findByToUser(username);
    }
}
