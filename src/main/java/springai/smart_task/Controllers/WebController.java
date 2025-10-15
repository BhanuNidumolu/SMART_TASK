package springai.smart_task.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import springai.smart_task.DTO.SubTaskResponse;
import springai.smart_task.Entities.Task;
import springai.smart_task.Entities.User;
import springai.smart_task.Repositories.TaskRepo;
import springai.smart_task.Service.TaskService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final TaskRepo taskRepo;
    private final TaskService taskService;

    @GetMapping("/")
    public String indexPage() {
        return "index";
    }

    @GetMapping("/tasks")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        List<Task> parentTasks = taskRepo.findByUserIdAndParentTaskIsNull(user.getId());
        model.addAttribute("tasks", parentTasks);
        return "tasks";
    }

    @GetMapping("/tasks/{taskId}")
    public String showSubTasks(@AuthenticationPrincipal User user, @PathVariable Long taskId, Model model) {
        Task parentTask = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task plan not found"));

        if (!parentTask.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied");
        }

        List<Task> subTasks = taskRepo.findByParentTask(parentTask);
        Set<String> criticalTaskNames = taskService.findCriticalPath(subTasks);

        List<SubTaskResponse> response = subTasks.stream()
                .map(subTask -> new SubTaskResponse(
                        subTask.getId(),
                        subTask.getName(),
                        subTask.getDuration(),
                        subTask.getDeadline(),
                        subTask.getScheduledTime(),
                        subTask.getPriority(),
                        subTask.getDependencies(),
                        subTask.getStatus(),
                        criticalTaskNames.contains(subTask.getName())
                ))
                .collect(Collectors.toList());

        model.addAttribute("parentTask", parentTask);
        model.addAttribute("subTasks", response);

        return "subtasks";
    }
}