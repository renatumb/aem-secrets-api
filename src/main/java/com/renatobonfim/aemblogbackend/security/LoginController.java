package com.renatobonfim.aemblogbackend.security;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
public class LoginController {

    @Autowired
    private JWTService jwtService;


    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = jwtService.generateJWTToken(loginRequest.username, loginRequest.password);

        return ResponseEntity.ok(Map.of("token", token));
    }

    record LoginRequest(
            @Email String username,
            @NotBlank String password) {
    }
}
