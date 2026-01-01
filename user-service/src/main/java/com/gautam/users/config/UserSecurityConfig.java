package com.gautam.users.config;

import com.gautam.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@RequiredArgsConstructor
public class UserSecurityConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username).map(user -> User.builder().username(user.getUsername()).password(user.getPassword()).roles("USER").build()).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
