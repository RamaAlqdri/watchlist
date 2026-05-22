package com.example.moviewatchlist.service;

import com.example.moviewatchlist.dto.AuthResponse;
import com.example.moviewatchlist.dto.LoginRequest;
import com.example.moviewatchlist.dto.RegisterRequest;
import com.example.moviewatchlist.entity.User;
import com.example.moviewatchlist.repository.UserRepository;
import com.example.moviewatchlist.security.SimpleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SimpleTokenService tokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username sudah digunakan");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = tokenService.createToken(savedUser);

        return toAuthResponse(savedUser, token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Username atau password salah"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Username atau password salah");
        }

        String token = tokenService.createToken(user);

        return toAuthResponse(user, token);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getName(),
                user.getUsername()
        );
    }
}
