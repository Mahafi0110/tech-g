package com.techg.controller;

import com.techg.entity.*;
import com.techg.repository.*;
import com.techg.service.ImageUploadService;
import com.techg.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository products;
    private final ServiceRepository services;
    private final FeatureRepository features;
    private final QuoteRequestRepository enquiries;
    private final ReviewRepository reviews;
    private final ImageUploadService imageUpload;
    private final SettingsService settings;

    public AdminController(ProductRepository products, ServiceRepository services, FeatureRepository features,
                           QuoteRequestRepository enquiries, ReviewRepository reviews,
                           ImageUploadService imageUpload, SettingsService settings) {
        this.products = products;
        this.services = services;
        this.features = features;
        this.enquiries = enquiries;
        this.reviews = reviews;
        this.imageUpload = imageUpload;
        this.settings = settings;
    }

    @GetMapping({"", "/"})
    public String root() { return "redirect:/admin/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("productCount", products.count());
        model.addAttribute("serviceCount", services.count());
        model.addAttribute("enquiryCount", enquiries.count());
        model.addAttribute("reviewCount", reviews.count());
        model.addAttribute("s", settings.all());
        model.addAttribute("groups", settings.groups());
        model.addAttribute("imageSlots", settings.imageSlots());
        model.addAttribute("images", settings.images());
        model.addAttribute("products", products.findAll());
        model.addAttribute("services", services.findAll());
        model.addAttribute("features", features.findAll());
        model.addAttribute("enquiries", enquiries.findAllByOrderByCreatedAtDesc());
        model.addAttribute("reviews", reviews.findAllByOrderByCreatedAtDesc());
        return "admin/dashboard";
    }

    // ---- Texts: contact, social links, every section heading ----
    @PostMapping("/settings/save")
    public String saveSettings(@RequestParam Map<String, String> params,
                               @RequestParam(value = "_group", required = false) String group,
                               RedirectAttributes ra) {
        String anchor = (group != null && settings.groupExists(group)) ? group : "contact";
        try {
            settings.save(params);
            ra.addFlashAttribute("msg", "Saved. The change is live on the landing page.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard#" + anchor;
    }

    // ---- Pictures (hero, setup banner, why-choose-us) ----
    @PostMapping("/image/{key}/save")
    public String saveImage(@PathVariable String key,
                            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                            RedirectAttributes ra) {
        if (!settings.isImageKey(key)) {
            ra.addFlashAttribute("error", "Unknown image.");
        } else if (imageFile == null || imageFile.isEmpty()) {
            ra.addFlashAttribute("error", "Please choose an image first.");
        } else {
            try {
                settings.saveImage(key, imageUpload.upload(imageFile, "techg/site"));
                ra.addFlashAttribute("msg", "Picture updated. It is now live on the landing page.");
            } catch (IllegalArgumentException e) {
                ra.addFlashAttribute("error", e.getMessage());
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Image upload failed. Check your Cloudinary settings. (" + e.getMessage() + ")");
            }
        }
        return "redirect:/admin/dashboard#images";
    }

    @PostMapping("/image/{key}/reset")
    public String resetImage(@PathVariable String key, RedirectAttributes ra) {
        if (settings.isImageKey(key)) {
            settings.resetImage(key);
            ra.addFlashAttribute("msg", "Picture reset to the default.");
        }
        return "redirect:/admin/dashboard#images";
    }

    // ---- Products: one endpoint adds (no id) or edits (with id). Image goes to Cloudinary. ----
    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product p,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              RedirectAttributes ra) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                p.setImageUrl(imageUpload.upload(imageFile));
            } catch (IllegalArgumentException e) {
                ra.addFlashAttribute("error", e.getMessage());
                return "redirect:/admin/dashboard#products";
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Image upload failed. Check your Cloudinary settings. (" + e.getMessage() + ")");
                return "redirect:/admin/dashboard#products";
            }
        }
        if (p.getPrice() == null) p.setPrice(0.0);
        if (p.getStockStatus() == null) p.setStockStatus(false);
        products.save(p);
        ra.addFlashAttribute("msg", "Product saved. It is now live on the landing page.");
        return "redirect:/admin/dashboard#products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        products.deleteById(id);
        ra.addFlashAttribute("msg", "Product deleted.");
        return "redirect:/admin/dashboard#products";
    }

    // ---- Services ----
    @PostMapping("/services/save")
    public String saveService(@ModelAttribute ServiceItem s, RedirectAttributes ra) {
        services.save(s);
        ra.addFlashAttribute("msg", "Service saved. It is now live on the landing page.");
        return "redirect:/admin/dashboard#services";
    }

    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable Long id, RedirectAttributes ra) {
        services.deleteById(id);
        ra.addFlashAttribute("msg", "Service deleted.");
        return "redirect:/admin/dashboard#services";
    }

    // ---- "Why choose us" cards ----
    @PostMapping("/features/save")
    public String saveFeature(@ModelAttribute FeatureItem f, RedirectAttributes ra) {
        features.save(f);
        ra.addFlashAttribute("msg", "Card saved. It is now live on the landing page.");
        return "redirect:/admin/dashboard#features";
    }

    @PostMapping("/features/{id}/delete")
    public String deleteFeature(@PathVariable Long id, RedirectAttributes ra) {
        features.deleteById(id);
        ra.addFlashAttribute("msg", "Card deleted.");
        return "redirect:/admin/dashboard#features";
    }

    // ---- Enquiries & reviews ----
    @PostMapping("/enquiries/{id}/delete")
    public String deleteEnquiry(@PathVariable Long id, RedirectAttributes ra) {
        enquiries.deleteById(id);
        ra.addFlashAttribute("msg", "Enquiry deleted.");
        return "redirect:/admin/dashboard#enquiries";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes ra) {
        reviews.deleteById(id);
        ra.addFlashAttribute("msg", "Review deleted.");
        return "redirect:/admin/dashboard#reviews";
    }
}
