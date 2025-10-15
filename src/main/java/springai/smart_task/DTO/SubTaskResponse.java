package springai.smart_task.DTO;

import springai.smart_task.Entities.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;

public record SubTaskResponse(Long id, String name, double duration, LocalDateTime deadline, LocalDateTime scheduledTime, String priority, List<String> dependencies, TaskStatus status, boolean isCriticalPath) {}