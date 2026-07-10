package com.codesolutions.pmt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private AppUser recipient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private ProjectTask task;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(nullable = false, length = 1200)
    private String message;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant readAt;

    protected Notification() {
    }

    public Notification(AppUser recipient, Project project, ProjectTask task, String title, String message) {
        this.recipient = recipient;
        this.project = project;
        this.task = task;
        this.title = title;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public AppUser getRecipient() {
        return recipient;
    }

    public Project getProject() {
        return project;
    }

    public ProjectTask getTask() {
        return task;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void markAsRead() {
        readAt = Instant.now();
    }
}
