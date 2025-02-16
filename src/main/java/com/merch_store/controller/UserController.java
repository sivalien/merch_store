package com.merch_store.controller;

import com.merch_store.controller.dto.AuthRequest;
import com.merch_store.controller.dto.AuthResponse;
import com.merch_store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;

    @PostMapping(value = "/auth", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<AuthResponse> auth(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }
}
