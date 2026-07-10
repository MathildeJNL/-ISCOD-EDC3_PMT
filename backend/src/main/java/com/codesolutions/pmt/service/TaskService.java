package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.ActivityAction;
import com.codesolutions.pmt.domain.AppUser;
import com.codesolutions.pmt.domain.Project;
import com.codesolutions.pmt.domain.ProjectTask;
import com.codesolutions.pmt.domain.TaskActivity;
import com.codesolutions.pmt.domain.TaskAssignment;
import com.codesolutions.pmt.domain.TaskStatus;
import com.codesolutions.pmt.repository.ProjectRepository;
import com.codesolutions.pmt.repository.ProjectTaskRepository;
import com.codesolutions.pmt.repository.TaskActivityRepository;
import com.codesolutions.pmt.repository.TaskAssignmentRepository;
import com.codesolutions.pmt.repository.UserRepository;
import com.codesolutions.pmt.web.dto.TaskDtos.ActivityResponse;
import com.codesolutions.pmt.web.dto.TaskDtos.AssigneeResponse;
import com.codesolutions.pmt.web.dto.TaskDtos.AssignmentRequest;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskRequest;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskResponse;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskUpdateRequest;
import com.codesolutions.pmt.web.error.BadRequestException;
import com.codesolutions.pmt.web.error.NotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final ProjectRepository projectRepository;
    private final ProjectTaskRepository taskRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final TaskActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final NotificationService notificationService;

    public TaskService(
            ProjectRepository projectRepository,
            ProjectTaskRepository taskRepository,
            TaskAssignmentRepository assignmentRepository,
            TaskActivityRepository activityRepository,
            UserRepository userRepository,
            PermissionService permissionService,
            NotificationService notificationService
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.assignmentRepository = assignmentRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.permissionService = permissionService;
        this.notificationService = notificationService;
    }

    @Transactional
    public TaskResponse create(Long actorId, Long projectId, TaskRequest request) {
        permissionService.requireEditor(projectId, actorId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        AppUser actor = requireUser(actorId);

        ProjectTask task = taskRepository.save(new ProjectTask(
                project,
                request.title(),
                request.description(),
                request.dueDate(),
                request.priority(),
                actor
        ));
        log(task, actor, ActivityAction.CREATED, "Task created");

        if (request.assigneeIds() != null && !request.assigneeIds().isEmpty()) {
            assignInternal(task, actor, request.assigneeIds());
        }
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> board(Long actorId, Long projectId, TaskStatus status) {
        permissionService.requireMembership(projectId, actorId);
        List<ProjectTask> tasks = status == null
                ? taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId)
                : taskRepository.findByProjectIdAndStatusOrderByCreatedAtDesc(projectId, status);
        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse get(Long actorId, Long taskId) {
        ProjectTask task = requireTask(taskId);
        permissionService.requireMembership(task.getProject().getId(), actorId);
        return toResponse(task);
    }

    @Transactional
    public TaskResponse update(Long actorId, Long taskId, TaskUpdateRequest request) {
        ProjectTask task = requireTask(taskId);
        permissionService.requireEditor(task.getProject().getId(), actorId);
        AppUser actor = requireUser(actorId);
        List<String> changes = new ArrayList<>();
        TaskStatus previousStatus = task.getStatus();

        if (request.title() != null && !request.title().isBlank()) {
            task.setTitle(request.title());
            changes.add("title");
        }
        if (request.description() != null && !request.description().isBlank()) {
            task.setDescription(request.description());
            changes.add("description");
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
            changes.add("due date");
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
            changes.add("priority");
        }
        if (request.status() != null) {
            task.setStatus(request.status());
            changes.add("status");
        }

        if (changes.isEmpty()) {
            throw new BadRequestException("At least one task field must be updated");
        }

        ActivityAction action = previousStatus != task.getStatus()
                ? ActivityAction.STATUS_CHANGED
                : ActivityAction.UPDATED;
        log(task, actor, action, "Updated " + String.join(", ", changes));
        return toResponse(task);
    }

    @Transactional
    public TaskResponse assign(Long actorId, Long taskId, AssignmentRequest request) {
        ProjectTask task = requireTask(taskId);
        permissionService.requireEditor(task.getProject().getId(), actorId);
        AppUser actor = requireUser(actorId);
        if (request.assigneeIds() == null || request.assigneeIds().isEmpty()) {
            throw new BadRequestException("At least one assignee is required");
        }
        assignInternal(task, actor, request.assigneeIds());
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> history(Long actorId, Long taskId) {
        ProjectTask task = requireTask(taskId);
        permissionService.requireMembership(task.getProject().getId(), actorId);
        return activityRepository.findByTaskIdOrderByCreatedAtDesc(taskId).stream()
                .map(activity -> new ActivityResponse(
                        activity.getId(),
                        activity.getAction(),
                        activity.getActor().getUsername(),
                        activity.getDetails(),
                        activity.getCreatedAt()
                ))
                .toList();
    }

    private void assignInternal(ProjectTask task, AppUser actor, List<Long> assigneeIds) {
        Set<Long> uniqueIds = new LinkedHashSet<>(assigneeIds);
        for (Long assigneeId : uniqueIds) {
            AppUser assignee = requireUser(assigneeId);
            permissionService.requireMembership(task.getProject().getId(), assigneeId);
            if (!assignmentRepository.existsByTaskIdAndAssigneeId(task.getId(), assigneeId)) {
                assignmentRepository.save(new TaskAssignment(task, assignee, actor));
                notificationService.notifyTaskAssigned(task, actor, assignee);
                log(task, actor, ActivityAction.ASSIGNED, "Assigned to " + assignee.getUsername());
            }
        }
    }

    private ProjectTask requireTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    private AppUser requireUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void log(ProjectTask task, AppUser actor, ActivityAction action, String details) {
        activityRepository.save(new TaskActivity(task, actor, action, details));
    }

    private TaskResponse toResponse(ProjectTask task) {
        List<AssigneeResponse> assignees = assignmentRepository.findByTaskIdOrderByAssignedAtAsc(task.getId()).stream()
                .map(assignment -> new AssigneeResponse(
                        assignment.getAssignee().getId(),
                        assignment.getAssignee().getUsername(),
                        assignment.getAssignee().getEmail()
                ))
                .toList();
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                assignees,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
