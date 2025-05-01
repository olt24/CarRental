package com.carrental.controller;

import com.carrental.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    public AdminController(UserService userService) { this.userService = userService; }

    @GetMapping("/licenses")
    public String pending(Model m) {
        m.addAttribute("list", userService.pendingLicenses());
        return "admin/licenses";
    }

    @PostMapping("/licenses/{id}/approve")
    public String approve(@PathVariable Long id) {
        userService.approveLicense(id);
        return "redirect:/admin/licenses";
    }
}
