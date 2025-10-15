package springai.smart_task.DTO;
import java.util.List;
public record TaskSummaryResponse(int taskCount, List<TaskInfo> tasks) {
    public record TaskInfo(Long id, String name) {}
}