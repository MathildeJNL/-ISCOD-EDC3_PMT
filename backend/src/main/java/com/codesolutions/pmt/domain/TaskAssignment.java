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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "task_assignments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_task_assignments_task_user",
                columnNames = {"task_id", "assignee_id"}
        )
)
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee_id", nullable = false)
    private AppUser assignee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by", nullable = false)
    private AppUser assignedBy;

    @Column(nullable = false, updatable = false)
    private Instant assignedAt = Instant.now();

    protected TaskAssignment() {
    }

    public TaskAssignment(ProjectTask task, AppUser assignee, AppUser assignedBy) {
        this.task = task;
        this.assignee = assignee;
        this.assignedBy = assignedBy;
    }

    public Long getId() {
        return id;
    }

    public ProjectTask getTask() {
        return task;
    }

    public AppUser getAssignee() {
        return assignee;
    }

    public AppUser getAssignedBy() {
        return assignedBy;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }
}
