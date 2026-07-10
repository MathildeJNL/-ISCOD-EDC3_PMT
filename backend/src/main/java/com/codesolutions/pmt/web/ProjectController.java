package com.codesolutions.pmt.web;

import com.codesolutions.pmt.service.ProjectService;
import com.codesolutions.pmt.web.dto.ProjectDtos.ChangeRoleRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.InviteMemberRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.MemberResponse;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectDetailResponse;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectResponse;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> list(@RequestHeader("X-User-Id") Long userId) {
        return projectService.list(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDetailResponse create(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ProjectRequest request
    ) {
        return projectService.create(userId, request);
    }

    @GetMapping("/{projectId}")
    public ProjectDetailResponse get(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId
    ) {
        return projectService.get(userId, projectId);
    }

    @GetMapping("/{projectId}/members")
    public List<MemberResponse> members(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId
    ) {
        return projectService.members(userId, projectId);
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse invite(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId,
            @Valid @RequestBody InviteMemberRequest request
    ) {
        return projectService.inviteMember(userId, projectId, request);
    }

    @PatchMapping("/{projectId}/members/{memberId}/role")
    public MemberResponse changeRole(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @Valid @RequestBody ChangeRoleRequest request
    ) {
        return projectService.changeRole(userId, projectId, memberId, request);
    }
}
