package com.techg.controller;

import com.techg.entity.QuoteRequest;
import com.techg.repository.QuoteRequestRepository;
import com.techg.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EnquiryController {

    private final QuoteRequestRepository repo;
    private final EmailService emailService;

    public EnquiryController(QuoteRequestRepository repo, EmailService emailService) {
        this.repo = repo;
        this.emailService = emailService;
    }

    @PostMapping("/submit-enquiry")
    public String submit(@ModelAttribute QuoteRequest form, RedirectAttributes ra) {
        boolean bad = isBlank(form.getName()) || isBlank(form.getPhone())
                || isBlank(form.getProductName()) || form.getQuantity() == null || form.getQuantity() < 1;
        if (bad) {
            ra.addFlashAttribute("toast", "Please fill in your name, phone, product and quantity.");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/#contact";
        }
        form.setId(null); // always insert a new row
        QuoteRequest saved = repo.save(form);
        emailService.sendEnquiryNotification(saved);
        ra.addFlashAttribute("toast", "Thank you! Your enquiry has been sent. We will get back to you soon.");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/#contact";
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
