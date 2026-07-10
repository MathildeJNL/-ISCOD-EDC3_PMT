package com.codesolutions.pmt.web.dto;

import java.time.Instant;

public record NotificationDto(
        Long id,
        Long projectId,
        Long taskId,
        String title,
        String message,
        Instant createdAt,
        Instant readAt
) {
}
