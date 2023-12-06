package com.springboot.blog.controller;

import com.springboot.blog.payload.JwtAuthResponse;
import com.springboot.blog.payload.LoginDto;
import com.springboot.blog.payload.RegisterDto;
import com.springboot.blog.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {

        var token = authService.login(loginDto);
        var jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return ResponseEntity.ok(jwtAuthResponse);
    }

    @PostMapping("register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDtoDto) {

        var response = authService.register(registerDtoDto);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }
}
