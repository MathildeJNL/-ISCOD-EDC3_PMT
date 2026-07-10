package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.AppUser;
import com.codesolutions.pmt.domain.Notification;
import com.codesolutions.pmt.domain.ProjectTask;
import com.codesolutions.pmt.repository.NotificationRepository;
import com.codesolutions.pmt.web.dto.NotificationDto;
import com.codesolutions.pmt.web.error.ForbiddenOperationException;
import com.codesolutions.pmt.web.error.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EmailGateway emailGateway;

    public NotificationService(NotificationRepository notificationRepository, EmailGateway emailGateway) {
        this.notificationRepository = notificationRepository;
        this.emailGateway = emailGateway;
    }

    @Transactional
    public NotificationDto notifyTaskAssigned(ProjectTask task, AppUser actor, AppUser recipient) {
        String title = "Task assigned";
        String message = actor.getUsername() + " assigned you to " + task.getTitle();
        Notification notification = notificationRepository.save(new Notification(
                recipient,
                task.getProject(),
                task,
                title,
                message
        ));
        emailGateway.send(recipient.getEmail(), title, message);
        return toDto(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> list(Long userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public NotificationDto markRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ForbiddenOperationException("Notification belongs to another user");
        }
        notification.markAsRead();
        return toDto(notification);
    }

    private NotificationDto toDto(Notification notification) {
        Long taskId = notification.getTask() == null ? null : notification.getTask().getId();
        return new NotificationDto(
                notification.getId(),
                notification.getProject().getId(),
                taskId,
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
