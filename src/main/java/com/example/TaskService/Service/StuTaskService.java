package com.example.TaskService.Service;

import com.example.TaskService.DTO.StuTaskDTO;
import com.example.TaskService.DTO.TaskDTO;
import com.example.TaskService.Model.StuTask;
import com.example.TaskService.Model.StuTaskId;
import com.example.TaskService.Model.Task;
import com.example.TaskService.Repository.StuTaskRepository;
import com.example.TaskService.Repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StuTaskService {

    private final StuTaskRepository stuTaskRepository;
    private final TaskRepository taskRepository;

    public StuTaskDTO addOrUpdateStuTask(StuTaskDTO dto) {
        // Validate task exists and fetch it
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        StuTaskId stuTaskId = new StuTaskId(dto.getStudentId(), dto.getTaskId());
        StuTask stuTask = stuTaskRepository.findById(stuTaskId).orElse(null);

        if (stuTask != null) {
            if (stuTask.getDeletedAt() != null) {
                stuTask.setDeletedAt(null);
            }
        } else {
            stuTask = StuTask.builder()
                    .id(stuTaskId)
                    .task(task)  // Set the Task reference explicitly
                    .deletedAt(null)
                    .build();
        }

        stuTaskRepository.save(stuTask);
        return dto;
    }
    public List<StuTaskDTO> getStudentsByTaskId(UUID taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }

        List<StuTask> stuTasks = stuTaskRepository.findByTaskId(taskId);
        return stuTasks.stream()
                .filter(stuTask -> stuTask.getDeletedAt() == null)
                .map(stuTask -> new StuTaskDTO(
                        stuTask.getId().getStudentId(),
                        stuTask.getId().getTaskId()
                ))
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByStudentId(UUID studentId) {
        List<StuTask> stuTasks = stuTaskRepository.findByStudentId(studentId);
        return stuTasks.stream()
                .filter(stuTask -> stuTask.getDeletedAt() == null)
                .map(stuTask -> {
                    Task task = taskRepository.findById(stuTask.getId().getTaskId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Task not found for ID: " + stuTask.getId().getTaskId()
                            ));
                    return convertToDTO(task);
                })
                .collect(Collectors.toList());
    }

    public void softDeleteStuTask(UUID studentId, UUID taskId) {
        StuTaskId id = new StuTaskId(studentId, taskId);
        StuTask stuTask = stuTaskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student-task assignment not found"
                ));

        if (stuTask.getDeletedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This task assignment is already deleted"
            );
        }

        stuTask.setDeletedAt(LocalDateTime.now());
        stuTaskRepository.save(stuTask);
    }

    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .taskId(task.getTaskId())
                .taskName(task.getTaskName())
                .attachments(task.getAttachments())
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .studentStatus(task.getStudentStatus())
                .completeStatus(task.getCompleteStatus())
                .modifiedAt(task.getModifiedAt())
                .openStatus(task.getOpenStatus())
                .deletedAt(task.getDeletedAt())
                .taskObjective(task.getTaskObjective())
                .modifiedBy(task.getModifiedBy() != null ? task.getModifiedBy().toString() : null)
                .projectId(task.getProjectId())
                .build();
    }
}