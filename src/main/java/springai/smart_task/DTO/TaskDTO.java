package springai.smart_task.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

// This DTO matches the JSON structure the AI is expected to return.
@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskDTO(
    String name,
    double duration,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    LocalDateTime deadline,
    List<String> dependencies,
    String priority
) {}