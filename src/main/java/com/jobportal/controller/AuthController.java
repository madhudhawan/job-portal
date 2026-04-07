package com.jobportal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jobportal.model.User;
import com.jobportal.service.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ==============================
    // Show login page
    // URL : http://localhost:8080/login
    // ==============================
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";         // looks for templates/login.html
    }

    // ==============================
    // Show register page
    // URL : http://localhost:8080/register
    // ==============================
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Send empty User object to the form
        model.addAttribute("user", new User());
        return "register";      // looks for templates/register.html
    }

    // ==============================
    // Handle register form submission
    // URL : POST http://localhost:8080/register
    // ==============================
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") User user,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "register";
        }

        // Call service to register user
        String result = userService.registerUser(user);

        if (result.equals("success")) {
            // Registration successful — go to login page
            return "redirect:/login?registered=true";
        } else {
            // Email already exists
            model.addAttribute("error", result);
            return "register";
        }
    }

    // ==============================
    // Dashboard redirect based on role
    // URL : http://localhost:8080/dashboard
    // ==============================
    @GetMapping("/dashboard")
    public String dashboard() {

        // Get currently logged in user
        String role = getCurrentUserRole();

        if (role.equals("ROLE_RECRUITER")) {
            return "redirect:/recruiter/dashboard";
        } else {
            return "redirect:/candidate/dashboard";
        }
    }

    // Helper method to get current logged in user role
    private String getCurrentUserRole() {
        // Get authentication from Spring Security
        org.springframework.security.core.Authentication auth =
            org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        // Get first role of the user
        return auth.getAuthorities().iterator().next().getAuthority();
    }
}