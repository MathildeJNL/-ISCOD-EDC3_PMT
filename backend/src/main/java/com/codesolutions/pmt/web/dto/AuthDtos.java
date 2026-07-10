package com.codesolutions.pmt.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 80) String username,
            @Email @NotBlank String email,
            @NotBlank
            @Size(min = 8, max = 120)
            @Pattern(
                    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                    message = "must contain at least one lowercase letter, one uppercase letter and one number"
            )
            String password
    ) {
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }

    public record UserResponse(
            Long id,
            String username,
            String email,
            Instant createdAt
    ) {
    }

    public record AuthResponse(
            String accessToken,
            UserResponse user
    ) {
    }
}
