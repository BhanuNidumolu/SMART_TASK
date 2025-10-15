package springai.smart_task.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import springai.smart_task.Entities.Task;
import springai.smart_task.Entities.User;

@Service
@RequiredArgsConstructor
public class AsyncSchedulingService {
    private static final Logger logger = LoggerFactory.getLogger(AsyncSchedulingService.class);
    private final ChatService chatService;
    private final TaskService taskService;
    private final NotificationService notificationService;

    @Async
    public void generatePlanAndSave(User user, String prompt) {
        logger.info("Starting async plan generation for user: {}", user.getUsername());
        try {
            chatService.chat(user, prompt)
                .collectList()
                .map(responses -> String.join("", responses))
                .flatMap(jsonResponse -> {
                    Task parentTask = taskService.scheduleTasks(user, jsonResponse);
                    if (parentTask != null) {
                        notificationService.notifyPlanReady(user.getUsername(), parentTask.getId(), parentTask.getName());
                    }
                    return Mono.empty();
                }).block();
            logger.info("Successfully completed async plan generation for user: {}", user.getUsername());
        } catch (Exception e) {
            logger.error("Async plan generation failed for user: {}", user.getUsername(), e);
        }
    }
}