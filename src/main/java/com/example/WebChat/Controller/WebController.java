package com.example.WebChat.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login(HttpSession session) {
        if (session.getAttribute("name") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute("name") == null) {
            return "redirect:/login";
        }
        return "home";
    }
}
