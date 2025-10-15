package springai.smart_task.DTO;
import jakarta.validation.constraints.NotBlank;
public record PlanRequest(@NotBlank String prompt) {}