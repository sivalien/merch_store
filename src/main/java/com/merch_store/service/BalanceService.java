package com.merch_store.service;

import com.merch_store.controller.dto.*;
import com.merch_store.exception.BadRequestException;
import com.merch_store.repository.InventoryRepository;
import com.merch_store.repository.UserInventoryRepository;
import com.merch_store.repository.HistoryRepository;
import com.merch_store.repository.dto.UserBalance;
import com.merch_store.repository.dto.UserInventory;
import com.merch_store.repository.dto.History;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final InventoryRepository inventoryRepository;
    private final UserInventoryRepository userInventoryRepository;
    private final HistoryRepository historyRepository;
    private final UserService userService;

    @Transactional
    public void purchase(String username, String inventory) {
        String inventoryLowerCase = inventory.toLowerCase();
        Integer price = inventoryRepository.findPrice(inventoryLowerCase);
        if (price == null)
            throw new BadRequestException("Нет такого товара " + inventory);
        UserBalance user = userService.getUserBalance(username);
        if (price > user.coins())
            throw new BadRequestException("На вашем балансе недостаточно средств ");

        userInventoryRepository.createOrUpdate(username, inventory, 1);
        userService.changeBalance(username, user.coins() - price);
    }

    @Transactional
    public void transfer(String fromUserName, SendCoinRequest sendCoinRequest) {
        UserBalance fromUser = userService.getUserBalance(fromUserName);
        if (sendCoinRequest.toUser() == null)
            throw new BadRequestException("Отсутствует обязательное поле toUser");
        if (sendCoinRequest.amount() == null)
            throw new BadRequestException("Отсутствует обязательное поле amount");
        if (sendCoinRequest.amount() > fromUser.coins())
            throw new BadRequestException("На вашем балансе недостаточно средств");
        UserBalance toUser = userService.getUserBalance(sendCoinRequest.toUser());
        if (toUser == null)
            throw new BadRequestException("Пользователь " + sendCoinRequest.toUser() + " не найден");

        Long amount = sendCoinRequest.amount();
        if (amount <= 0)
            throw new BadRequestException("Можно перевести только положительную сумму");

        userService.changeBalance(fromUser.username(), fromUser.coins() - amount);
        userService.changeBalance(toUser.username(), toUser.coins() + amount);
        historyRepository.create(fromUser.username(), toUser.username(), amount);
    }

    @Transactional
    public InfoResponse getUserInfo(String username) {
        UserBalance user = userService.getUserBalance(username);
        List<UserInventory> purchases = userInventoryRepository.findByUserName(username);
        List<History> fromUserTransfers= historyRepository.findByFromUser(username);
        List<History> toUserTransfers = historyRepository.findByToUser(username);
        return new InfoResponse(
                user.coins(),
                purchases.stream().map(x -> new InventoryEntry(x.inventoryType(), x.quantity())).toList(),
                new CoinHistoryResponse(
                        toUserTransfers
                                .stream()
                                .map(x -> new ReceivedEntry(x.fromUser(), x.amount()))
                                .toList(),
                        fromUserTransfers
                                .stream()
                                .map(x -> new SentEntry(x.toUser(), x.amount()))
                                .toList()
                )
        );
    }
}
