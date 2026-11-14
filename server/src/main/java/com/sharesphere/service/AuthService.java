package com.sharesphere.service;

import com.sharesphere.dto.request.LoginRequest;
import com.sharesphere.dto.request.RegisterRequest;
import com.sharesphere.dto.response.AuthResponse;
import com.sharesphere.dto.response.UserSummaryResponse;
import com.sharesphere.entity.Role;
import com.sharesphere.entity.User;
import com.sharesphere.exception.BadRequestException;
import com.sharesphere.exception.ConflictException;
import com.sharesphere.exception.ResourceNotFoundException;
import com.sharesphere.repository.UserRepository;
import com.sharesphere.security.JwtTokenProvider;
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
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email already exists.");
        }

        Role role = Role.USER;
        if ("DELIVERY_PARTNER".equalsIgnoreCase(request.getRole())) {
            role = Role.DELIVERY_PARTNER;
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(role)
                .address(request.getAddress())
                .rating(5.0)
                .ratingCount(0)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getRole().name());

        return AuthResponse.builder()
                .success(true)
                .message("Registration successful")
                .token(token)
                .user(mapToSummary(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if (!user.getIsActive()) {
            throw new BadRequestException("Account is inactive or suspended.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        String token = tokenProvider.generateToken(user.getId(), user.getRole().name());

        return AuthResponse.builder()
                .success(true)
                .message("Login successful")
                .token(token)
                .user(mapToSummary(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToSummary(user);
    }

    public UserSummaryResponse mapToSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .rating(user.getRating())
                .ratingCount(user.getRatingCount())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
