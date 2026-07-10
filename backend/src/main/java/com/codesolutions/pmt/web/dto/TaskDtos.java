package com.codesolutions.pmt.web.dto;

import com.codesolutions.pmt.domain.ActivityAction;
import com.codesolutions.pmt.domain.TaskPriority;
import com.codesolutions.pmt.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class TaskDtos {
    private TaskDtos() {
    }

    public record TaskRequest(
            @NotBlank @Size(max = 160) String title,
            @NotBlank @Size(max = 2000) String description,
            @NotNull LocalDate dueDate,
            @NotNull TaskPriority priority,
            List<Long> assigneeIds
    ) {
    }

    public record TaskUpdateRequest(
            @Size(max = 160) String title,
            @Size(max = 2000) String description,
            LocalDate dueDate,
            TaskPriority priority,
            TaskStatus status
    ) {
    }

    public record AssignmentRequest(List<Long> assigneeIds) {
    }

    public record TaskResponse(
            Long id,
            Long projectId,
            String title,
            String description,
            LocalDate dueDate,
            TaskPriority priority,
            TaskStatus status,
            List<AssigneeResponse> assignees,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    public record AssigneeResponse(Long id, String username, String email) {
    }

    public record ActivityResponse(
            Long id,
            ActivityAction action,
            String actor,
            String details,
            Instant createdAt
    ) {
    }
}
