package com.example.TaskService.Controller;

import com.example.TaskService.DTO.ApiResponse;
import com.example.TaskService.DTO.StuTaskDTO;
import com.example.TaskService.DTO.TaskDTO;
import com.example.TaskService.Service.StuTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stu-task")
@RequiredArgsConstructor
public class StuTaskController {

    private final StuTaskService stuTaskService;

    @PostMapping
    public ResponseEntity<ApiResponse<StuTaskDTO>> addOrUpdateStuTask(@RequestBody StuTaskDTO dto) {
        StuTaskDTO updatedTask = stuTaskService.addOrUpdateStuTask(dto);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Task assigned/updated successfully", updatedTask)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getTasksOrStudents(
            @RequestParam(required = false) UUID taskId,
            @RequestParam(required = false) UUID studentId) {

        if (taskId != null) {
            List<StuTaskDTO> students = stuTaskService.getStudentsByTaskId(taskId);
            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Students retrieved successfully", students)
            );
        } else if (studentId != null) {
            List<TaskDTO> tasks = stuTaskService.getTasksByStudentId(studentId);
            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Tasks retrieved successfully", tasks)
            );
        } else {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(400, "Invalid request: Provide either taskId or studentId", null)
            );
        }
    }

    @DeleteMapping("/{studentId}/{taskId}")
    public ResponseEntity<ApiResponse<Void>> softDeleteStuTask(
            @PathVariable UUID studentId,
            @PathVariable UUID taskId) {
        stuTaskService.softDeleteStuTask(studentId, taskId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "Task assignment deleted successfully", null)
        );
    }
}