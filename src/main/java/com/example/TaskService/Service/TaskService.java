package com.example.TaskService.Service;

import com.example.TaskService.DTO.TaskDTO;
import com.example.TaskService.Exception.BadRequestException;
import com.example.TaskService.Exception.ResourceNotFoundException;
import com.example.TaskService.Model.Task;
import com.example.TaskService.Repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpServletRequest request;

    @Value("${project.service.url}")
    private String projectServiceUrl;

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        // Validate project exists
        verifyProjectExists(taskDTO.getProjectId());

        if (taskRepository.existsByTaskNameAndProjectId(taskDTO.getTaskName(), taskDTO.getProjectId())) {
            throw new BadRequestException("Task with the same name already exists in this project");
        }

        Task task = new Task();
        task.setTaskName(taskDTO.getTaskName());
        task.setTaskObjective(taskDTO.getTaskObjective());
        task.setDueDate(taskDTO.getDueDate());
        task.setCompleteStatus(taskDTO.getCompleteStatus() != null ? taskDTO.getCompleteStatus() : "Not Completed");
        task.setProjectId(taskDTO.getProjectId());
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }

    @Transactional
    public TaskDTO updateTask(UUID taskId, TaskDTO taskDTO) {
        Task task = taskRepository.findByTaskIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // If project ID is being updated, verify the new project exists
        if (taskDTO.getProjectId() != null && !taskDTO.getProjectId().equals(task.getProjectId())) {
            verifyProjectExists(taskDTO.getProjectId());
            task.setProjectId(taskDTO.getProjectId());
        }

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

        // Get the user ID from the authentication context (you would need to implement this)
        // For now, keeping your UUID.randomUUID() implementation
        task.setModifiedBy(UUID.randomUUID());

        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
        // Verify project exists before returning tasks for it
        verifyProjectExists(projectId);

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
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(UUID taskId) {
        return taskRepository.findByTaskIdAndDeletedAtIsNull(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
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

    /**
     * Verifies that a project exists by making a call to the ProjectService
     */
    private void verifyProjectExists(UUID projectId) {
        if (projectId == null) {
            throw new BadRequestException("Project ID is required");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            String token = request.getHeader("Authorization");
            if (token != null) {
                headers.set("Authorization", token);
            }
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    projectServiceUrl + "?projectId=" + projectId,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResourceNotFoundException("Project verification failed with status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Project not found");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new BadRequestException("Access denied while verifying project. Please check service authentication.");
            }
            throw new BadRequestException("Failed to verify project: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BadRequestException("Failed to verify project: " + e.getMessage());
        }
    }
}