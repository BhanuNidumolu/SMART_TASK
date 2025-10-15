package springai.smart_task.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
    @NotBlank @Size(min = 3, max = 20) String username,
    @NotBlank @Size(min = 6) String password
) {}