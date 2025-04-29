package com.example.TaskService.Controller;

import com.example.TaskService.DTO.ApiResponse;
import com.example.TaskService.DTO.TaskDTO;
import com.example.TaskService.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@RequestBody TaskDTO taskDTO) {
        TaskDTO createdTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Task created successfully", createdTask)
        );
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable UUID taskId,
            @RequestParam(value = "taskName", required = false) String taskName,
            @RequestParam(value = "taskObjective", required = false) String taskObjective,
            @RequestParam(value = "dueDate", required = false) String dueDateStr,
            @RequestParam(value = "completeStatus", required = false) String completeStatus,
            @RequestParam(value = "studentStatus", required = false) String studentStatus,
            @RequestParam(value = "attachments", required = false) MultipartFile attachments) {

        TaskDTO existingTaskDTO = taskService.convertToDTO(taskService.getTaskById(taskId));

        if (taskName != null) existingTaskDTO.setTaskName(taskName);
        if (taskObjective != null) existingTaskDTO.setTaskObjective(taskObjective);
        if (dueDateStr != null) {
            try {
                existingTaskDTO.setDueDate(
                        LocalDateTime.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                );
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Invalid date format", e);
            }
        }
        if (completeStatus != null) existingTaskDTO.setCompleteStatus(completeStatus);
        if (studentStatus != null) existingTaskDTO.setStudentStatus(studentStatus);
        if (attachments != null) {
            try {
                existingTaskDTO.setAttachments(attachments.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("File upload failed", e);
            }
        }

        TaskDTO updatedTask = taskService.updateTask(taskId, existingTaskDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Task updated successfully", updatedTask)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasks(
            @RequestParam(required = false) UUID projectId) {
        List<TaskDTO> tasks = projectId == null
                ? taskService.getAllTasks()
                : taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Tasks retrieved successfully", tasks)
        );
    }

    @GetMapping("/{taskId}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable UUID taskId) {
        byte[] attachments = taskService.getTaskById(taskId).getAttachments();
        if (attachments == null || attachments.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"task_attachment_" + taskId + "\"")
                .body(attachments);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Task deleted successfully", null)
        );
    }
}