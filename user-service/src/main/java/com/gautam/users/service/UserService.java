package com.gautam.users.service;

import com.gautam.users.exception.InvalidCredentialsException;
import com.gautam.users.exception.UserAlreadyExistsException;
import com.gautam.users.model.User;
import com.gautam.users.repository.UserRepository;
import com.gautam.users.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.gautam.users.constant.AuthConstant.MESSAGE_INVALID_CREDENTIALS;
import static com.gautam.users.constant.AuthConstant.MESSAGE_USER_EXISTS;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new InvalidCredentialsException(MESSAGE_INVALID_CREDENTIALS));

        if (!user.getPassword().equals(password)) {
            throw new InvalidCredentialsException(MESSAGE_INVALID_CREDENTIALS);
        }
        return jwtUtil.generateToken(username);
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException(MESSAGE_USER_EXISTS);
        }
        userRepository.save(user);
    }
}
