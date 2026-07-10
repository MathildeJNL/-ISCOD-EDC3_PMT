package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.TaskAssignment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByTaskIdOrderByAssignedAtAsc(Long taskId);

    boolean existsByTaskIdAndAssigneeId(Long taskId, Long assigneeId);
}
