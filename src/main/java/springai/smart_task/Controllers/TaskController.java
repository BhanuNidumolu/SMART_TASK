
package springai.smart_task.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springai.smart_task.DTO.PlanRequest;
import springai.smart_task.DTO.TaskSummaryResponse;
import springai.smart_task.DTO.UpdateTaskStatusRequest;
import springai.smart_task.Entities.Task;
import springai.smart_task.Entities.User;
import springai.smart_task.Repositories.TaskRepo;
import springai.smart_task.Service.AsyncSchedulingService;
import springai.smart_task.Service.TaskService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final AsyncSchedulingService asyncService;
    private final TaskService taskService;
    private final TaskRepo taskRepo;

    @PostMapping("/schedule")
    public ResponseEntity<Void> schedule(@AuthenticationPrincipal User user, @Valid @RequestBody PlanRequest request) {
        asyncService.generatePlanAndSave(user, request.prompt());
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<TaskSummaryResponse> getTaskSummary(@AuthenticationPrincipal User user) {
        List<Task> parentTasks = taskRepo.findByUserIdAndParentTaskIsNull(user.getId());
        List<TaskSummaryResponse.TaskInfo> taskInfos = parentTasks.stream()
                .map(task -> new TaskSummaryResponse.TaskInfo(task.getId(), task.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new TaskSummaryResponse(taskInfos.size(), taskInfos));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<Void> updateTaskStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        taskService.updateTaskStatus(user, taskId, request.status());
        return ResponseEntity.ok().build();
    }
}