package com.carrental.controller;


import com.carrental.entity.User;
import com.carrental.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/register")
    public String showRegister() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam("dlFile") MultipartFile dlFile,
                           Model m) {
        try {
            userService.register(name, email, password, dlFile);
            return "redirect:/login?msg=registered";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String msg,
                            Model model) {
        if (error != null)
            model.addAttribute("error", "Invalid credentials");

        if (logout != null)
            model.addAttribute("msg", "Logged out");

        if (msg != null && logout == null)
            model.addAttribute("msg", msg);

        return "login";
    }

}
