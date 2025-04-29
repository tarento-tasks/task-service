package com.example.TaskService.Service;

import com.example.TaskService.DTO.TaskDTO;
import com.example.TaskService.Model.Task;
import com.example.TaskService.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        if (taskRepository.existsByTaskNameAndProjectId(taskDTO.getTaskName(), taskDTO.getProjectId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task with the same name already exists in this project");
        }

        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompleteStatus(taskDTO.getCompleteStatus() != null ? taskDTO.getCompleteStatus() : "Not Completed");
        task.setProjectId(taskDTO.getProjectId());
        task.setCreatedAt(LocalDateTime.now());
        // Remove this line: task.setModifiedBy(UUID.randomUUID()); // For testing purposes

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(UUID taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findByTaskIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (taskDTO.getTaskName() != null) {
            task.setTaskName(taskDTO.getTaskName());
        }
        if (taskDTO.getTaskObjective() != null) {
            task.setTaskObjective(taskDTO.getTaskObjective());
        }
        if (taskDTO.getDueDate() != null) {
            task.setDueDate(taskDTO.getDueDate());
        }
        if (taskDTO.getCompleteStatus() != null) {
            task.setCompleteStatus(taskDTO.getCompleteStatus());
        }
        if (taskDTO.getAttachments() != null) {
            task.setAttachments(taskDTO.getAttachments());
        }
        if (taskDTO.getStudentStatus() != null) {
            task.setStudentStatus(taskDTO.getStudentStatus());
        }

        task.setModifiedAt(LocalDateTime.now());
        task.setModifiedBy(UUID.randomUUID()); // For testing purposes

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
        return taskRepository.findByProjectIdAndDeletedAtIsNull(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAllActive().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(UUID taskId) {
        return taskRepository.findByTaskIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setTaskObjective(task.getTaskObjective());
        dto.setDueDate(task.getDueDate());
        dto.setCompleteStatus(task.getCompleteStatus());
        dto.setStudentStatus(task.getStudentStatus());
        dto.setAttachments(task.getAttachments());
        dto.setProjectId(task.getProjectId());
        dto.setModifiedBy(task.getModifiedBy() != null ? task.getModifiedBy().toString() : null);
        dto.setCreatedAt(task.getCreatedAt());
        dto.setModifiedAt(task.getModifiedAt());
        dto.setDeletedAt(task.getDeletedAt());
        return dto;
    }
}