package com.merch_store.service;

import com.merch_store.controller.dto.AuthRequest;
import com.merch_store.controller.dto.AuthResponse;
import com.merch_store.exception.BadRequestException;
import com.merch_store.exception.UnauthorizedException;
import com.merch_store.repository.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse authenticate(AuthRequest authRequest) {
        if (authRequest.username() == null)
            throw new BadRequestException("Обязательное поле username отсутствует");
        if (authRequest.password() == null)
            throw new BadRequestException("Обязательное поле password отсутствует");
        User user = userService.getUser(authRequest.username());
        if (user == null) {
            return register(authRequest);
        }
        return login(user, authRequest);
    }

    private AuthResponse register(AuthRequest authRequest) {
        User user = userService.create(authRequest.username(), passwordEncoder.encode(authRequest.password()));
        return new AuthResponse(tokenService.generateToken(user));
    }

    private AuthResponse login(User user, AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.username(),
                    authRequest.password()
            ));
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Неверный пароль");
        }

        return new AuthResponse(tokenService.generateToken(user));
    }
}
