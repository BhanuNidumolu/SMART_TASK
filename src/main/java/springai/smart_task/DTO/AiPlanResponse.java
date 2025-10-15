package springai.smart_task.DTO;

import java.util.List;

public record AiPlanResponse(String planTitle, List<TaskDTO> tasks) {}