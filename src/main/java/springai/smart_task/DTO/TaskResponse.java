package springai.smart_task.DTO;
import java.time.LocalDateTime;
import java.util.List;
public record TaskResponse(
    Long id, String name, double duration, LocalDateTime deadline,
    LocalDateTime scheduledTime, Long parentTaskId, List<String> dependencies,
    String priority, boolean isCriticalPath
) {}