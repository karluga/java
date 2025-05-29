package com.may.informatic.controllers;

import com.may.informatic.entities.User;
import com.may.informatic.services.UserService;
import com.may.informatic.utils.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User user = userService.findByUsername(username);
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            if (user.getRole() == 1) {
                return "redirect:/rooms";
            } else {
                return "redirect:/booking";
            }
        } else {
            model.addAttribute("status", "Invalid credentials.");
            return "login";
        }
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username, @RequestParam String password, Model model) {
        boolean success = userService.registerUser(username, password, 0);
        if (success) {
            return "redirect:/booking"; // Bug: new user keeps previous session
        } else {
            model.addAttribute("registerStatus", "Username already exists.");
        }
        return "login";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}