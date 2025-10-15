package springai.smart_task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springai.smart_task.Entities.Session;
import springai.smart_task.Entities.User;
import springai.smart_task.Repositories.SessionRepo;
import springai.smart_task.Repositories.UserRepo;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MemoryService {

    private final ConcurrentHashMap<Long, ChatMemory> inMemory = new ConcurrentHashMap<>();
    private final UserRepo userRepo;
    private final SessionRepo sessionRepo;

    public ChatMemory getMemory(Long userId) {
        return inMemory.computeIfAbsent(userId, id -> {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            Session session = user.getSession();
            if (session != null && session.getSessionData() != null && !session.getSessionData().isEmpty()) {
                return ChatMemory.fromJson(session.getSessionData());
            }
            return new ChatMemory();
        });
    }

    @Transactional
    public void persistMemory(Long userId) {
        ChatMemory memory = inMemory.get(userId);
        if (memory == null || memory.getMessages().isEmpty()) return;

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Session session = user.getSession();

        if (session == null) {
            session = new Session();
            user.setSession(session);
        }

        String newJson = memory.toJson();
        if (!newJson.equals(session.getSessionData())) {
            session.setSessionData(newJson);
            sessionRepo.save(session);
        }
    }
}