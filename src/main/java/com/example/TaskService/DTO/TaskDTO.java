package com.example.TaskService.DTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private UUID taskId;
    private String taskName;
    private byte[] attachments;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private String studentStatus;
    private String completeStatus;  // Changed from Boolean to String
    private LocalDateTime modifiedAt;
    private Boolean openStatus;
    private LocalDateTime deletedAt;
    private String taskObjective;
    private String modifiedBy;
    private UUID projectId;
}