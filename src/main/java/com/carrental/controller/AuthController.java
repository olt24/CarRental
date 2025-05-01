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
            return "redirect:/login?registered";
        } catch (Exception e) {
            m.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLogin(@RequestParam(required=false) String error,
                            @RequestParam(required=false) String logout,
                            Model m) {
        if (error != null)  m.addAttribute("error","Invalid credentials");
        if (logout != null) m.addAttribute("msg","Logged out");
        return "login";
    }
}
