package com.codesolutions.pmt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "task_activities")
public class TaskActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private AppUser actor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityAction action;

    @Column(nullable = false, length = 1000)
    private String details;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected TaskActivity() {
    }

    public TaskActivity(ProjectTask task, AppUser actor, ActivityAction action, String details) {
        this.task = task;
        this.actor = actor;
        this.action = action;
        this.details = details;
    }

    public Long getId() {
        return id;
    }

    public ProjectTask getTask() {
        return task;
    }

    public AppUser getActor() {
        return actor;
    }

    public ActivityAction getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
