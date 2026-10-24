package com.techg.controller;

import com.techg.entity.*;
import com.techg.repository.*;
import com.techg.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final ProductRepository products;
    private final ServiceRepository services;
    private final ReviewRepository reviews;
    private final FeatureRepository features;
    private final SettingsService settings;

    public HomeController(ProductRepository products, ServiceRepository services, ReviewRepository reviews,
                          FeatureRepository features, SettingsService settings) {
        this.products = products;
        this.services = services;
        this.reviews = reviews;
        this.features = features;
        this.settings = settings;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> productList = products.findAll();
        List<ServiceItem> serviceList = services.findAll();
        List<Review> reviewList = reviews.findAllByOrderByCreatedAtDesc();
        double avg = reviewList.stream().mapToInt(Review::getRating).average().orElse(0);

        Map<String, String> s = settings.all();
        Map<String, String> img = settings.images();

        model.addAttribute("s", s);
        model.addAttribute("img", img);
        model.addAttribute("hasSocials", settings.hasSocials(s));
        model.addAttribute("products", productList);
        model.addAttribute("services", serviceList);
        model.addAttribute("features", features.findAll());
        model.addAttribute("reviews", reviewList);
        model.addAttribute("avgRating", String.format("%.1f", avg));
        model.addAttribute("productNames", productList.stream().map(Product::getName).toList());
        model.addAttribute("serviceNames", serviceList.stream().map(ServiceItem::getTitle).toList());
        return "index";
    }
}
