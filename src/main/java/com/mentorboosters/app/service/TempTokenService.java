package com.mentorboosters.app.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TempTokenService {

    private final Map<String, TempToken> tokenStore = new ConcurrentHashMap<>();

    private static final long TOKEN_EXPIRY_MINUTES = 20;

    public String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofMinutes(TOKEN_EXPIRY_MINUTES));
        tokenStore.put(token, new TempToken(username, expiry));
//        System.out.println("Current Token Store:");
//        tokenStore.forEach((key, value) -> {
//            System.out.println("Token: " + key);
//            System.out.println("Username: " + value.username());
//            System.out.println("Expires At: " + value.expiry());
//            System.out.println("----------");
//        });

        return token;
    }

    public boolean isValid(String token) {
        TempToken temp = tokenStore.get(token);
        if (temp == null || temp.expiry().isBefore(Instant.now())) {
            tokenStore.remove(token); // Clean up expired token
            return false;
        }
        return true;
    }

    public String getUsername(String token) {
        TempToken temp = tokenStore.get(token);
        return (temp != null && temp.expiry().isAfter(Instant.now())) ? temp.username() : null;
    }

    // Optional cleanup method for expired tokens (can run as scheduled task)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        tokenStore.entrySet().removeIf(e -> e.getValue().expiry().isBefore(now));
    }

    // static class is used only in nested class
    private record TempToken(String username, Instant expiry) {}

    // the above can be written as
//    private static class TempToken {
//        private final String username;
//        private final Instant expiry;
//
//        public TempToken(String username, Instant expiry) {
//            this.username = username;
//            this.expiry = expiry;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//        public Instant getExpiry() {
//            return expiry;
//        }
//    }
}

