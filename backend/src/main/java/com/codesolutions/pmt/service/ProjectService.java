package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.AppUser;
import com.codesolutions.pmt.domain.Notification;
import com.codesolutions.pmt.domain.Project;
import com.codesolutions.pmt.domain.ProjectMember;
import com.codesolutions.pmt.repository.NotificationRepository;
import com.codesolutions.pmt.repository.ProjectMemberRepository;
import com.codesolutions.pmt.repository.ProjectRepository;
import com.codesolutions.pmt.repository.UserRepository;
import com.codesolutions.pmt.web.dto.ProjectDtos.ChangeRoleRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.InviteMemberRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.MemberResponse;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectDetailResponse;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectRequest;
import com.codesolutions.pmt.web.dto.ProjectDtos.ProjectResponse;
import com.codesolutions.pmt.web.error.ConflictException;
import com.codesolutions.pmt.web.error.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.codesolutions.pmt.domain.ProjectRole.ADMIN;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PermissionService permissionService;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository memberRepository,
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            PermissionService permissionService
    ) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.permissionService = permissionService;
    }

    @Transactional
    public ProjectDetailResponse create(Long actorId, ProjectRequest request) {
        AppUser actor = userRepository.findById(actorId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Project project = projectRepository.save(new Project(
                request.name(),
                request.description(),
                request.startDate()
        ));
        memberRepository.save(new ProjectMember(project, actor, ADMIN));
        return detail(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> list(Long actorId) {
        return projectRepository.findVisibleProjects(actorId).stream()
                .map(this::summary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse get(Long actorId, Long projectId) {
        permissionService.requireMembership(projectId, actorId);
        Project project = requireProject(projectId);
        return detail(project);
    }

    @Transactional
    public MemberResponse inviteMember(Long actorId, Long projectId, InviteMemberRequest request) {
        permissionService.requireAdmin(projectId, actorId);
        Project project = requireProject(projectId);
        AppUser invited = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new NotFoundException("Invited user must be registered first"));

        if (memberRepository.findByProjectIdAndUserId(projectId, invited.getId()).isPresent()) {
            throw new ConflictException("User is already a project member");
        }

        ProjectMember member = memberRepository.save(new ProjectMember(project, invited, request.role()));
        notificationRepository.save(new Notification(
                invited,
                project,
                null,
                "Project invitation",
                "You have been added to " + project.getName() + " as " + request.role()
        ));
        return member(member);
    }

    @Transactional
    public MemberResponse changeRole(Long actorId, Long projectId, Long memberId, ChangeRoleRequest request) {
        permissionService.requireAdmin(projectId, actorId);
        ProjectMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Project member not found"));
        if (!member.getProject().getId().equals(projectId)) {
            throw new NotFoundException("Project member not found in this project");
        }
        member.setRole(request.role());
        return member(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> members(Long actorId, Long projectId) {
        permissionService.requireMembership(projectId, actorId);
        return memberRepository.findByProjectIdOrderByJoinedAtAsc(projectId).stream()
                .map(this::member)
                .toList();
    }

    private Project requireProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private ProjectResponse summary(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getCreatedAt()
        );
    }

    private ProjectDetailResponse detail(Project project) {
        return new ProjectDetailResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getCreatedAt(),
                memberRepository.findByProjectIdOrderByJoinedAtAsc(project.getId()).stream()
                        .map(this::member)
                        .toList()
        );
    }

    private MemberResponse member(ProjectMember member) {
        return new MemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}
