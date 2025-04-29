package com.example.TaskService.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuTaskDTO {
    private UUID studentId;
    private UUID taskId;
}