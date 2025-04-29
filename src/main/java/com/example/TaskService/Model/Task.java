package com.example.TaskService.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID taskId;

    @Column(nullable = false)
    private String taskName;

    @Lob
    @Column(name = "attachments", columnDefinition = "BYTEA")
    private byte[] attachments;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime dueDate;
    private String studentStatus;
    private String completeStatus;
    private LocalDateTime modifiedAt;
    private Boolean openStatus;
    private LocalDateTime deletedAt;

    @Column(nullable = true)  // Add this annotation
    private UUID modifiedBy;

    @Column(length = 500)
    private String taskObjective;

    //private UUID modifiedBy;
    private UUID projectId;
}