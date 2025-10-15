package springai.smart_task.DTO;


import springai.smart_task.Entities.TaskStatus;

public record UpdateTaskStatusRequest(TaskStatus status) {}