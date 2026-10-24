package com.techg.config;

import com.techg.entity.*;
import com.techg.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Fills an empty database with starter content so the page is never blank. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository products;
    private final ServiceRepository services;
    private final ReviewRepository reviews;
    private final FeatureRepository features;

    public DataSeeder(ProductRepository products, ServiceRepository services,
                      ReviewRepository reviews, FeatureRepository features) {
        this.products = products;
        this.services = services;
        this.reviews = reviews;
        this.features = features;
    }

    @Override
    public void run(String... args) {
        if (products.count() == 0) {
            p("Laptops", "Portable computers for study, work and everyday use.", "Computers", "/images/products/laptop.png");
            p("Desktop Computers", "Desktop systems for home and office workspaces.", "Computers", "/images/products/desktop.png");
            p("Keyboard & Mouse", "Everyday input devices to complete your desk.", "Peripherals", "/images/products/keyboard-mouse.png");
            p("Printers", "Reliable printers for home and office documents.", "Peripherals", "/images/products/printer.png");
            p("VGA / Display Cables", "Quality cables to connect screens and computers.", "Networking", "/images/products/vga-cable.png");
            p("LAN / Network Cables", "Durable network cables for fast, stable connections.", "Networking", "/images/products/lan-cable.png");
        }
        if (services.count() == 0) {
            s("Laptop Service", "Speed up, fix and upgrade your laptop.", "fa-solid fa-laptop");
            s("Desktop Service", "Diagnosis and repair for desktop computers.", "fa-solid fa-desktop");
            s("Slow Performance", "Make a slow computer fast and responsive again.", "fa-solid fa-gauge-high");
            s("Hardware Issues", "Screens, keyboards, drives, batteries and more.", "fa-solid fa-microchip");
            s("Network Solutions", "Stay connected with reliable network setups.", "fa-solid fa-wifi");
            s("Maintenance Plans", "Prevent issues with regular maintenance.", "fa-solid fa-screwdriver-wrench");
        }
        if (features.count() == 0) {
            ft("Complete Computer Solutions", "Products and accessories for a computer setup.", "fa-solid fa-laptop");
            ft("Product Variety", "Laptops, desktops, peripherals, and networking accessories.", "fa-solid fa-shield-halved");
            ft("Sales & Service", "Computer purchasing and service support.", "fa-solid fa-headset");
            ft("Easy Enquiries", "A straightforward way to enquire about products and services.", "fa-solid fa-bolt");
        }
        if (reviews.count() == 0) {
            r("Sample Customer", "Your City", 5, "Quick, honest service. My laptop feels brand new again. (Sample review: delete it in the admin panel.)");
        }
    }

    private void p(String n, String d, String c, String img) {
        Product x = new Product();
        x.setName(n); x.setDescription(d); x.setCategory(c); x.setImageUrl(img);
        x.setPrice(0.0); x.setStockStatus(true);
        products.save(x);
    }

    private void s(String t, String d, String i) {
        ServiceItem x = new ServiceItem();
        x.setTitle(t); x.setDescription(d); x.setIconClass(i);
        services.save(x);
    }

    private void ft(String t, String d, String i) {
        FeatureItem x = new FeatureItem();
        x.setTitle(t); x.setDescription(d); x.setIconClass(i);
        features.save(x);
    }

    private void r(String n, String loc, int rating, String text) {
        Review x = new Review();
        x.setCustomerName(n); x.setLocation(loc); x.setRating(rating); x.setReviewText(text);
        reviews.save(x);
    }
}
