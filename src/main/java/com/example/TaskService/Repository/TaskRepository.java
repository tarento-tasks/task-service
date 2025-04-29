package com.example.TaskService.Repository;

import com.example.TaskService.Model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Optional<Task> findByTaskIdAndDeletedAtIsNull(UUID taskId);
    boolean existsByTaskNameAndProjectId(String taskName, UUID projectId);
    List<Task> findByProjectIdAndDeletedAtIsNull(UUID projectId);

    @Query("SELECT t FROM Task t WHERE t.deletedAt IS NULL")
    List<Task> findAllActive();

    List<Task> findByProjectId(UUID projectId);
}