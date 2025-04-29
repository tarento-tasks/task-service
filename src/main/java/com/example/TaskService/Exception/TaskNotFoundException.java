package com.example.TaskService.Exception;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID taskId) {
        super("Task not found with ID: " + taskId);
    }
}