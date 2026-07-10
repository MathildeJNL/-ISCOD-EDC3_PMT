package com.codesolutions.pmt.web;

import com.codesolutions.pmt.service.NotificationService;
import com.codesolutions.pmt.web.dto.NotificationDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> list(@RequestHeader("X-User-Id") Long userId) {
        return notificationService.list(userId);
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationDto read(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long notificationId
    ) {
        return notificationService.markRead(userId, notificationId);
    }
}
