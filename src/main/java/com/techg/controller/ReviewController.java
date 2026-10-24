package com.techg.controller;

import com.techg.entity.Review;
import com.techg.repository.ReviewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReviewController {

    private final ReviewRepository repo;

    public ReviewController(ReviewRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/submit-review")
    public String submit(@ModelAttribute Review form, RedirectAttributes ra) {
        Integer r = form.getRating();
        boolean bad = form.getCustomerName() == null || form.getCustomerName().isBlank()
                || form.getReviewText() == null || form.getReviewText().isBlank()
                || r == null || r < 1 || r > 5;
        if (bad) {
            ra.addFlashAttribute("toast", "Please add your name, a star rating (1-5) and a review.");
            ra.addFlashAttribute("toastType", "error");
            return "redirect:/#reviews";
        }
        form.setId(null);
        repo.save(form);
        ra.addFlashAttribute("toast", "Thanks for your review!");
        ra.addFlashAttribute("toastType", "success");
        return "redirect:/#reviews";
    }
}
