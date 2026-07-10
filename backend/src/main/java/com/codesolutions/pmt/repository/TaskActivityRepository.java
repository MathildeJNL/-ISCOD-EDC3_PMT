package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.TaskActivity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
