package springai.smart_task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springai.smart_task.DTO.RegistrationRequest;
import springai.smart_task.Entities.User;
import springai.smart_task.Repositories.UserRepo;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegistrationRequest request) {
        if (userRepo.findByName(request.username()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        User user = new User();
        user.setName(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepo.save(user);
    }
}