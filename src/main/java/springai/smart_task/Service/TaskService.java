package springai.smart_task.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import springai.smart_task.DTO.AiPlanResponse;
import springai.smart_task.DTO.TaskDTO;
import springai.smart_task.Entities.Task;
import springai.smart_task.Entities.TaskStatus;
import springai.smart_task.Entities.User;
import springai.smart_task.Repositories.TaskRepo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepo taskRepo;
    private final ObjectMapper objectMapper;

    @Transactional
    public Task scheduleTasks(User user, String aiJsonResponse) {
        String cleanedJson = cleanAiResponse(aiJsonResponse);
        AiPlanResponse plan = parseAiResponse(cleanedJson);

        if (plan.tasks() == null || plan.tasks().isEmpty()) {
            logger.warn("AI returned a plan with no tasks for user: {}", user.getUsername());
            return null;
        }

        Task parentTask = createParentTask(plan.planTitle(), user, plan.tasks());
        taskRepo.save(parentTask);

        List<Task> subTasks = plan.tasks().stream()
                .map(dto -> convertDtoToEntity(dto, user, parentTask))
                .collect(Collectors.toList());
        parentTask.setSubTasks(subTasks);
        taskRepo.saveAll(subTasks);

        scheduleAllTasks(subTasks);
        taskRepo.saveAll(subTasks);

        return parentTask;
    }

    @Transactional
    public void updateTaskStatus(User user, Long taskId, TaskStatus newStatus) {
        Task task = taskRepo.findById(taskId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        task.setStatus(newStatus);
        taskRepo.save(task);
    }

    private String cleanAiResponse(String rawResponse) {
        logger.debug("Raw AI Response: {}", rawResponse);
        int firstBrace = rawResponse.indexOf('{');
        int lastBrace = rawResponse.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace > firstBrace) {
            String cleaned = rawResponse.substring(firstBrace, lastBrace + 1);
            logger.debug("Cleaned JSON: {}", cleaned);
            return cleaned;
        }
        throw new IllegalArgumentException("Could not find a valid JSON object in the AI response.");
    }

    private AiPlanResponse parseAiResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, AiPlanResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("Failed to parse AI JSON response: {}", jsonResponse, e);
            throw new IllegalArgumentException("Invalid plan format from AI.", e);
        }
    }

    private Task createParentTask(String planTitle, User user, List<TaskDTO> subTasks) {
        Task parent = new Task();
        parent.setName(planTitle);
        parent.setUser(user);
        parent.setDuration(subTasks.stream().mapToDouble(TaskDTO::duration).sum());
        parent.setDeadline(subTasks.stream()
                .map(TaskDTO::deadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().plusDays(7)));
        parent.setPriority("High");
        return parent;
    }

    private Task convertDtoToEntity(TaskDTO dto, User user, Task parent) {
        Task task = new Task();
        task.setName(dto.name());
        task.setDuration(dto.duration());
        task.setDeadline(dto.deadline());
        task.setPriority(dto.priority());
        task.setDependencies(dto.dependencies() != null ? new ArrayList<>(dto.dependencies()) : new ArrayList<>());
        task.setUser(user);
        task.setParentTask(parent);
        return task;
    }

    private void scheduleAllTasks(List<Task> tasks) {
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getName, t -> t, (t1, t2) -> t1));
        Set<Task> scheduled = new HashSet<>();
        tasks.forEach(task -> scheduleTask(task, taskMap, scheduled));
    }

    private void scheduleTask(Task task, Map<String, Task> taskMap, Set<Task> scheduled) {
        if (scheduled.contains(task)) return;
        LocalDateTime earliestStartTime = LocalDateTime.now();
        for (String depName : task.getDependencies()) {
            Task dependency = taskMap.get(depName);
            if (dependency != null) {
                if (!scheduled.contains(dependency)) {
                    scheduleTask(dependency, taskMap, scheduled);
                }
                LocalDateTime depEndTime = dependency.getScheduledTime().plusHours((long) Math.ceil(dependency.getDuration()));
                if (depEndTime.isAfter(earliestStartTime)) {
                    earliestStartTime = depEndTime;
                }
            }
        }
        task.setScheduledTime(earliestStartTime);
        scheduled.add(task);
    }

    public Set<String> findCriticalPath(List<Task> tasks) {
        if (tasks.isEmpty()) return Collections.emptySet();
        Map<String, Task> taskMap = tasks.stream().collect(Collectors.toMap(Task::getName, t -> t));
        Map<String, LocalDateTime> latestFinishTimes = new HashMap<>();
        LocalDateTime projectDeadline = tasks.stream()
            .map(t -> t.getScheduledTime().plusHours((long) Math.ceil(t.getDuration())))
            .max(LocalDateTime::compareTo).orElse(LocalDateTime.now());
        tasks.forEach(task -> calculateLatestFinish(task, taskMap, latestFinishTimes, projectDeadline));
        Set<String> criticalPathNames = new HashSet<>();
        for (Task task : tasks) {
            LocalDateTime earliestFinish = task.getScheduledTime().plusHours((long) Math.ceil(task.getDuration()));
            LocalDateTime latestFinish = latestFinishTimes.get(task.getName());
            if (latestFinish != null && (earliestFinish.isEqual(latestFinish) || earliestFinish.isAfter(latestFinish))) {
                criticalPathNames.add(task.getName());
            }
        }
        return criticalPathNames;
    }

    private void calculateLatestFinish(Task task, Map<String, Task> taskMap, Map<String, LocalDateTime> latestFinishTimes, LocalDateTime projectDeadline) {
        if (latestFinishTimes.containsKey(task.getName())) return;
        LocalDateTime latestFinish = projectDeadline;
        List<Task> dependents = taskMap.values().stream()
                .filter(t -> t.getDependencies().contains(task.getName()))
                .collect(Collectors.toList());
        if (!dependents.isEmpty()) {
            latestFinish = dependents.stream()
                .map(dependent -> {
                    calculateLatestFinish(dependent, taskMap, latestFinishTimes, projectDeadline);
                    return latestFinishTimes.get(dependent.getName()).minusHours((long) Math.ceil(dependent.getDuration()));
                })
                .min(LocalDateTime::compareTo)
                .orElse(projectDeadline);
        }
        latestFinishTimes.put(task.getName(), latestFinish);
    }
}