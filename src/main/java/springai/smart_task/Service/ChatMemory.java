package springai.smart_task.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import springai.smart_task.DTO.ChatMessageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMemory {

    private final List<Message> messages = new ArrayList<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public void addUserMessage(String content) {
        messages.add(new UserMessage(content));
    }

    public void addAiMessage(String content) {
        messages.add(new AssistantMessage(content));
    }

    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }

    public String toJson() {
        try {
            List<ChatMessageDTO> dtoList = messages.stream()
                .map(msg -> new ChatMessageDTO(msg.getMessageType().name(), msg.getText()))
                .collect(Collectors.toList());
            return mapper.writeValueAsString(dtoList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize ChatMemory to JSON", e);
        }
    }

    public static ChatMemory fromJson(String json) {
        try {
            List<ChatMessageDTO> dtoList = mapper.readValue(json, new TypeReference<>() {});
            ChatMemory memory = new ChatMemory();
            for (ChatMessageDTO dto : dtoList) {
                if ("USER".equalsIgnoreCase(dto.type())) {
                    memory.addUserMessage(dto.text());
                } else if ("ASSISTANT".equalsIgnoreCase(dto.type())) {
                    memory.addAiMessage(dto.text());
                }
            }
            return memory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize ChatMemory from JSON", e);
        }
    }
}