package com.merch_store.controller;

import com.merch_store.controller.dto.InfoResponse;
import com.merch_store.controller.dto.SendCoinRequest;
import com.merch_store.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping(value = "/info", produces = {"application/json"})
    public ResponseEntity<InfoResponse> getInfo(@RequestAttribute("username") String username) {
        return ResponseEntity.ok(balanceService.getUserInfo(username));
    }

    @PostMapping(value = "/sendCoin", consumes = {"application/json"})
    public ResponseEntity<String> sendCoin(
            @RequestAttribute("username") String username,
            @RequestBody @Valid SendCoinRequest sendCoinRequest
            ) {
        balanceService.transfer(username, sendCoinRequest);
        return ResponseEntity.ok("Coins were successfully sent");
    }

    @GetMapping("/buy/{item}")
    public ResponseEntity<String> buyItem(
            @RequestAttribute("username") String username,
            @PathVariable("item") String inventory
    ) {
        balanceService.purchase(username, inventory);
        return ResponseEntity.ok("Item was successfully bought");
    }

}
