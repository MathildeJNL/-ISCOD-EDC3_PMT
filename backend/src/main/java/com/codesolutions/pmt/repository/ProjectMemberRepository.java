package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.ProjectMember;
import com.codesolutions.pmt.domain.ProjectRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByProjectIdOrderByJoinedAtAsc(Long projectId);

    boolean existsByProjectIdAndUserIdAndRoleIn(
            Long projectId,
            Long userId,
            Collection<ProjectRole> roles
    );
}
