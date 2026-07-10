package com.codesolutions.pmt.web;

import com.codesolutions.pmt.domain.TaskStatus;
import com.codesolutions.pmt.service.TaskService;
import com.codesolutions.pmt.web.dto.TaskDtos.ActivityResponse;
import com.codesolutions.pmt.web.dto.TaskDtos.AssignmentRequest;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskRequest;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskResponse;
import com.codesolutions.pmt.web.dto.TaskDtos.TaskUpdateRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse> board(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status
    ) {
        return taskService.board(userId, projectId, status);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request
    ) {
        return taskService.create(userId, projectId, request);
    }

    @GetMapping("/tasks/{taskId}")
    public TaskResponse get(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long taskId
    ) {
        return taskService.get(userId, taskId);
    }

    @PatchMapping("/tasks/{taskId}")
    public TaskResponse update(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        return taskService.update(userId, taskId, request);
    }

    @PostMapping("/tasks/{taskId}/assignments")
    public TaskResponse assign(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long taskId,
            @RequestBody AssignmentRequest request
    ) {
        return taskService.assign(userId, taskId, request);
    }

    @GetMapping("/tasks/{taskId}/history")
    public List<ActivityResponse> history(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long taskId
    ) {
        return taskService.history(userId, taskId);
    }
}
