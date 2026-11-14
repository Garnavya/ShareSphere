package com.sharesphere.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    public String hashOtp(String plainOtp) {
        return passwordEncoder.encode(plainOtp);
    }

    public boolean verifyOtp(String plainOtp, String hashedOtp) {
        if (plainOtp == null || hashedOtp == null) {
            return false;
        }
        return passwordEncoder.matches(plainOtp, hashedOtp);
    }
}
