package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
            select member.project
            from ProjectMember member
            where member.user.id = :userId
            order by member.project.createdAt desc
            """)
    List<Project> findVisibleProjects(@Param("userId") Long userId);
}
