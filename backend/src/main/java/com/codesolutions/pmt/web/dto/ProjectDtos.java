package com.codesolutions.pmt.web.dto;

import com.codesolutions.pmt.domain.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ProjectDtos {
    private ProjectDtos() {
    }

    public record ProjectRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 1200) String description,
            @NotNull LocalDate startDate
    ) {
    }

    public record ProjectResponse(
            Long id,
            String name,
            String description,
            LocalDate startDate,
            Instant createdAt
    ) {
    }

    public record ProjectDetailResponse(
            Long id,
            String name,
            String description,
            LocalDate startDate,
            Instant createdAt,
            List<MemberResponse> members
    ) {
    }

    public record MemberResponse(
            Long id,
            Long userId,
            String username,
            String email,
            ProjectRole role,
            Instant joinedAt
    ) {
    }

    public record InviteMemberRequest(
            @Email @NotBlank String email,
            @NotNull ProjectRole role
    ) {
    }

    public record ChangeRoleRequest(@NotNull ProjectRole role) {
    }
}
