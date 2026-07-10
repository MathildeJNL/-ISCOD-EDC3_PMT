package com.codesolutions.pmt.service;

import com.codesolutions.pmt.domain.ProjectMember;
import com.codesolutions.pmt.domain.ProjectRole;
import com.codesolutions.pmt.repository.ProjectMemberRepository;
import com.codesolutions.pmt.web.error.ForbiddenOperationException;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionService {
    private static final Collection<ProjectRole> EDITOR_ROLES = List.of(ProjectRole.ADMIN, ProjectRole.MEMBER);

    private final ProjectMemberRepository memberRepository;

    public PermissionService(ProjectMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public ProjectMember requireMembership(Long projectId, Long userId) {
        return memberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ForbiddenOperationException("Project membership is required"));
    }

    @Transactional(readOnly = true)
    public void requireAdmin(Long projectId, Long userId) {
        ProjectMember member = requireMembership(projectId, userId);
        if (member.getRole() != ProjectRole.ADMIN) {
            throw new ForbiddenOperationException("Administrator role is required");
        }
    }

    @Transactional(readOnly = true)
    public void requireEditor(Long projectId, Long userId) {
        boolean allowed = memberRepository.existsByProjectIdAndUserIdAndRoleIn(projectId, userId, EDITOR_ROLES);
        if (!allowed) {
            throw new ForbiddenOperationException("Administrator or member role is required");
        }
    }
}
