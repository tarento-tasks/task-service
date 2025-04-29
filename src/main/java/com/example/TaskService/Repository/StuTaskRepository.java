package com.example.TaskService.Repository;

import com.example.TaskService.Model.StuTask;
import com.example.TaskService.Model.StuTaskId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StuTaskRepository extends JpaRepository<StuTask, StuTaskId> {
    @Query("SELECT st FROM StuTask st WHERE st.id.taskId = :taskId")
    List<StuTask> findByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT st FROM StuTask st WHERE st.id.studentId = :studentId")
    List<StuTask> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT st FROM StuTask st WHERE st.id.taskId = :taskId AND st.deletedAt IS NULL")
    List<StuTask> findActiveByTaskId(@Param("taskId") UUID taskId);

    @Query("SELECT st FROM StuTask st WHERE st.id.studentId = :studentId AND st.deletedAt IS NULL")
    List<StuTask> findActiveByStudentId(@Param("studentId") UUID studentId);

    @Modifying
    @Query("UPDATE StuTask st SET st.deletedAt = :deletedAt WHERE st.id.studentId = :studentId AND st.id.taskId = :taskId")
    void softDelete(@Param("studentId") UUID studentId,
                    @Param("taskId") UUID taskId,
                    @Param("deletedAt") LocalDateTime deletedAt);
}