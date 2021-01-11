package MailHeavenPackage.controllers;

import MailHeavenPackage.models.User;
import MailHeavenPackage.repos.UserRepository;
import MailHeavenPackage.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
public class LogInOutController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginGet(Principal principal, Model model){
        System.out.println("login get");
        if (principal != null){
            return "redirect:/";
        }
        return "loginPage";
    }

    //@PostMapping("/login")
    //public String loginPost(User user, Model model){
    //    System.out.println("login post");
    //    User userDB = userRepository.findByUsername(user.getUsername());
    //    if (userDB == null){
    //        System.out.println("нет в бд");
    //        model.addAttribute("errorMessage", true);
    //        model.addAttribute("errorDescription", "Пользователь с таким именем не найден!");
    //        return "loginPage";
    //    }
    //    if (!userDB.getPassword().equals(passwordEncoder.encode(user.getPassword()))){
    //        System.out.println("не совпали пароли");
    //        model.addAttribute("errorMessage", true);
    //        model.addAttribute("errorDescription", "Неверный пароль!");
    //        return "loginPage";
    //    }
    //    return "redirect:/main";
    //}
}
