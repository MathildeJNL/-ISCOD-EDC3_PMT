package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.AppUser;
import com.codesolutions.pmt.repository.UserRepository;
import com.codesolutions.pmt.web.dto.AuthDtos.AuthResponse;
import com.codesolutions.pmt.web.dto.AuthDtos.LoginRequest;
import com.codesolutions.pmt.web.dto.AuthDtos.RegisterRequest;
import com.codesolutions.pmt.web.dto.AuthDtos.UserResponse;
import com.codesolutions.pmt.web.error.ConflictException;
import com.codesolutions.pmt.web.error.ForbiddenOperationException;
import com.codesolutions.pmt.web.error.NotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Email is already used");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.username())) {
            throw new ConflictException("Username is already used");
        }

        AppUser user = userRepository.save(new AppUser(
                request.username(),
                request.email().toLowerCase(),
                passwordHasher.hash(request.password())
        ));
        return new AuthResponse(tokenFor(user), toResponse(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ForbiddenOperationException("Invalid credentials"));
        if (!passwordHasher.matches(request.password(), user.getPasswordHash())) {
            throw new ForbiddenOperationException("Invalid credentials");
        }
        return new AuthResponse(tokenFor(user), toResponse(user));
    }

    @Transactional(readOnly = true)
    public AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserResponse toResponse(AppUser user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreatedAt());
    }

    private String tokenFor(AppUser user) {
        String value = user.getId() + ":" + user.getEmail();
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
