package springai.smart_task.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springai.smart_task.DTO.RegistrationRequest;
import springai.smart_task.Service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registrationRequest")) {
            model.addAttribute("registrationRequest", new RegistrationRequest("", ""));
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute RegistrationRequest registrationRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registrationRequest", bindingResult);
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            return "redirect:/register";
        }
        try {
            userService.registerUser(registrationRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("registrationRequest", registrationRequest);
            redirectAttributes.addFlashAttribute("usernameError", e.getMessage());
            return "redirect:/register";
        }
    }
}