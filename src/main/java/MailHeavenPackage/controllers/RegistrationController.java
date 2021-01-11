package MailHeavenPackage.controllers;

import MailHeavenPackage.models.User;
import MailHeavenPackage.repos.UserRepository;
import MailHeavenPackage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registrationGet(Principal principal, Model model){
        System.out.println("registration get!");
        if (principal != null){
            return "redirect:/";
        }
        return "registrationPage";
    }

    @PostMapping("/registration")
    public String registrationPost(@RequestParam("duplicatePassword") String duplicatePassword, User user, Model model){
        System.out.println("registration post!");
        User userDB = userRepository.findByUsername(user.getUsername());
        if (userDB != null){
            model.addAttribute("errorMessage", true);
            model.addAttribute("errorDescription", "Пользователь с таким именем уже существует!");
            return "registrationPage";
        }
        if (!user.getPassword().equals(duplicatePassword)){
            System.out.println("1");
            model.addAttribute("errorMessage1", true);
            model.addAttribute("errorDescription1", "Введённые пароли не совпадают!");
            return "registrationPage";
        }
        if (!userService.addNewUser(user)){
            model.addAttribute("errorMessage1", true);
            model.addAttribute("errorDescription1", "В процессе регистрации что-то пошло не так, попробуйте снова");
            return "registrationPage";
        }
        return "redirect:/login";
    }
}
