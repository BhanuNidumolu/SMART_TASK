package springai.smart_task.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import springai.smart_task.Entities.User;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatClient chatClient;

    @Value("${default.system.prompt}")
    private String systemPromptTemplate;

    public Flux<String> chat(User user, String userPrompt) {
        String currentDateTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, hh:mm a z"));
        String finalSystemMessage = systemPromptTemplate.replace("{currentDateTime}", currentDateTime);

        logger.info("Final system prompt for user '{}': {}", user.getUsername(), finalSystemMessage);

        return chatClient.prompt()
                .system(finalSystemMessage)
                .user(userPrompt)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(120))
                .doOnError(e -> logger.error("Error during AI chat stream for user '{}': {}", user.getUsername(), e.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2))
                        .doBeforeRetry(signal -> logger.warn("Retrying AI call for user '{}'...", user.getUsername())));
    }
}