package com.gautam.users.controller;

import com.gautam.users.constant.AuthConstant;
import com.gautam.users.dto.ResponseDto;
import com.gautam.users.model.User;
import com.gautam.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto> register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseDto(AuthConstant.STATUS_201, AuthConstant.MESSAGE_201));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());

        return ResponseEntity.ok(
                new ResponseDto(
                        AuthConstant.STATUS_200, AuthConstant.MESSAGE_LOGIN_SUCCESS,
                        Map.of("token", token)));
    }
}

