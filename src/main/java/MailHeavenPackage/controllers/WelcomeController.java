package MailHeavenPackage.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class WelcomeController {

    @GetMapping("/")
    public String welcome(Principal principal, Model model){
        if (principal == null){
            model.addAttribute("logged", false);
        }
        else {
            model.addAttribute("logged", true);
            model.addAttribute("username", ", " + principal.getName());
        }
        return "welcomePage";
    }
}