package com.example.SmartNoticeBoard.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
    }
    
    public static void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("admin123"); // your default password
        System.out.println("BCrypt hash: " + hashed);
    }
    
}
