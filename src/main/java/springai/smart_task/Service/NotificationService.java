package springai.smart_task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyPlanReady(String username, Long newPlanId, String newPlanName) {
        String destination = "/topic/plans/" + username;
        Map<String, Object> payload = Map.of(
            "message", "Your new plan is ready!",
            "planId", newPlanId,
            "planName", newPlanName
        );
        messagingTemplate.convertAndSend(destination, payload);
    }
}