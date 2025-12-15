package com.gautam.user.service.controller;

import com.gautam.user.service.model.User;
import com.gautam.user.service.security.CustomUserDetailsService;
import com.gautam.user.service.security.JwtService;
import com.gautam.user.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        User existUser = userDetailsService.getUserByUsername(user.getUsername());
        if (existUser != null) {
            return existUser;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        User existUser = userDetailsService.getUserByUsername(user.getUsername());
        if (existUser != null) {
            return jwtService.generateToken(existUser.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        return jwtService.generateToken(userDetails.getUsername());
    }
}
