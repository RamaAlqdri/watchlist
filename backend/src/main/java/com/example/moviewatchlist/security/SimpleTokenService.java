package com.example.moviewatchlist.security;

import com.example.moviewatchlist.entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SimpleTokenService {

    private final Map<String, User> activeTokens = new ConcurrentHashMap<>();

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, user);
        return token;
    }

    public Optional<User> findUserByToken(String token) {
        return Optional.ofNullable(activeTokens.get(token));
    }

    public void removeToken(String token) {
        activeTokens.remove(token);
    }
}
