package com.techg.controller;

import com.techg.config.AdminAuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Value("${app.admin.username}")
    private String username;
    @Value("${app.admin.password}")
    private String password;

    @GetMapping("/login")
    public String loginPage(HttpSession session, @RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout, Model model) {
        if (Boolean.TRUE.equals(session.getAttribute(AdminAuthInterceptor.SESSION_KEY))) {
            return "redirect:/admin/dashboard";
        }
        if (error != null) model.addAttribute("error", "Wrong username or password.");
        if (logout != null) model.addAttribute("info", "You have been logged out.");
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest req) {
        if (same(username, this.username) && same(password, this.password)) {
            req.getSession(true).setAttribute(AdminAuthInterceptor.SESSION_KEY, Boolean.TRUE);
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/login?error";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login?logout";
    }

    private boolean same(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
