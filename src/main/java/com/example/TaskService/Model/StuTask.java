package com.example.TaskService.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stu_task",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"studentId", "taskId"},
                name = "uk_student_task"
        ))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StuTask {
    @EmbeddedId
    private StuTaskId id;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    @JoinColumn(name = "taskId", referencedColumnName = "taskId", insertable = false, updatable = false)
    private Task task;

    public UUID getStudentId() {
        return id.getStudentId();
    }

    public UUID getTaskId() {
        return id.getTaskId();
    }
}