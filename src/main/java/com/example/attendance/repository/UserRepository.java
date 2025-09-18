package com.example.attendance.repository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean authenticate(String userId, String rawPassword) {
        String hashed = hashPassword(rawPassword);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM m_user WHERE user_id = ? AND password = ?",
                Integer.class, userId, hashed);
        return count != null && count > 0;
    }

    public boolean exists(String userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM m_user WHERE user_id = ?",
                Integer.class, userId);
        return count != null && count > 0;
    }

    public boolean insert(String userId, String rawPassword) {
        String hashed = hashPassword(rawPassword);
        if (exists(userId)) {
            return false;
        }
        int updated = jdbcTemplate.update(
                "INSERT INTO m_user (user_id, password, confirmation) VALUES (?, ?, NULL)",
                userId, hashed);
        return updated > 0;
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] passwordDigest = digest.digest(rawPassword.getBytes());
            String sha1 = String.format("%040x", new BigInteger(1, passwordDigest));
            return sha1.substring(8);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to hash password", e);
        }
    }
}
