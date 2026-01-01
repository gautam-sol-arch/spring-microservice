package com.gautam.users.service;

import com.gautam.common.security.jwt.JwtTokenProvider;
import com.gautam.users.exception.InvalidCredentialsException;
import com.gautam.users.exception.UserAlreadyExistsException;
import com.gautam.users.model.User;
import com.gautam.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.gautam.users.constant.AuthConstant.MESSAGE_INVALID_CREDENTIALS;
import static com.gautam.users.constant.AuthConstant.MESSAGE_USER_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password) {
        try {
            User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidCredentialsException(MESSAGE_INVALID_CREDENTIALS));

            System.out.println(user.toString());

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.generateToken(authentication);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidCredentialsException(MESSAGE_INVALID_CREDENTIALS);
        }
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(MESSAGE_USER_EXISTS);
        }
        String password=passwordEncoder.encode(user.getPassword());
        System.out.println(password+"-------------------");
        user.setPassword(password);
        userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new InvalidCredentialsException(MESSAGE_INVALID_CREDENTIALS));
        userRepository.deleteById(user.getId());
    }
}
