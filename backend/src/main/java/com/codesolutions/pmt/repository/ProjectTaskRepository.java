package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.ProjectTask;
import com.codesolutions.pmt.domain.TaskStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<ProjectTask> findByProjectIdAndStatusOrderByCreatedAtDesc(Long projectId, TaskStatus status);
}
