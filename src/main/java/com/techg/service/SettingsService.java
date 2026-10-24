package com.techg.service;

import com.techg.entity.SiteSetting;
import com.techg.repository.SiteSettingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Defines every text and image the client can change in the admin panel,
 * reads the saved values (falling back to defaults) and validates new ones.
 */
@Service
public class SettingsService {

    public record Field(String key, String label, String def, String type, String hint) {}
    public record Group(String id, String title, String hint, List<Field> fields) {}
    public record ImageSlot(String key, String label, String hint, String def) {}

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final SiteSettingRepository repo;
    private final List<Group> groups = new ArrayList<>();
    private final List<ImageSlot> imageSlots = new ArrayList<>();
    private final Map<String, Field> fieldByKey = new LinkedHashMap<>();

    public SettingsService(SiteSettingRepository repo,
                           @Value("${app.phone}") String phone,
                           @Value("${app.email}") String email,
                           @Value("${app.address}") String address,
                           @Value("${app.admin-email}") String notifyEmail) {
        this.repo = repo;

        groups.add(g("contact", "Contact details", "Shown in the Contact section of the website.",
            f("phone", "Phone number", phone, "text", ""),
            f("email", "Email shown on website", email, "email", ""),
            f("address", "Address", address, "text", ""),
            f("business_hours", "Business hours (optional)", "", "text", "Mon - Sat, 9am - 7pm"),
            f("whatsapp", "WhatsApp number (optional)", "", "whatsapp", "Country code + number, e.g. 919876543210"),
            f("notify_email", "Email that receives enquiries", notifyEmail, "email", "")));

        groups.add(g("social", "Social media links", "Leave a link empty to hide its icon on the website.",
            f("social_facebook", "Facebook", "", "url", "https://facebook.com/yourpage"),
            f("social_instagram", "Instagram", "", "url", "https://instagram.com/yourpage"),
            f("social_x", "X (Twitter)", "", "url", "https://x.com/yourpage"),
            f("social_linkedin", "LinkedIn", "", "url", "https://linkedin.com/company/yourpage"),
            f("social_youtube", "YouTube", "", "url", "https://youtube.com/@yourchannel")));

        groups.add(g("hero-text", "Hero section (top of the page)", "The big heading is made of two parts: the black text and the orange text.",
            f("hero_eyebrow", "Small label above heading", "COMPUTER SALES & SERVICE", "text", ""),
            f("hero_title", "Heading (black part)", "One supplier for", "text", ""),
            f("hero_accent", "Heading (orange part)", "every piece", "text", ""),
            f("hero_subtitle", "Description", "We supply the products and accessories needed for a complete computer setup \u2014 from the screen you look at to the cable that connects you.", "textarea", ""),
            f("hero_btn1", "First button text", "Explore Products", "text", ""),
            f("hero_btn2", "Second button text", "Request a Quote", "text", ""),
            f("warranty_title", "Warranty badge title", "1-Year Service Warranty", "text", ""),
            f("warranty_note", "Warranty badge note", "(Warranty Protection Included)", "text", ""),
            f("hl1_title", "Highlight 1 title", "Laptop Services", "text", ""),
            f("hl1_desc", "Highlight 1 text", "Speed up, fix and upgrade your laptop.", "text", ""),
            f("hl2_title", "Highlight 2 title", "Network Solutions", "text", ""),
            f("hl2_desc", "Highlight 2 text", "Stay connected with reliable network solutions.", "text", ""),
            f("hl3_title", "Highlight 3 title", "Maintenance Plans", "text", ""),
            f("hl3_desc", "Highlight 3 text", "Prevent issues with regular maintenance.", "text", "")));

        groups.add(g("services-text", "Repair & services heading", "The service cards themselves are managed in the Services list below.",
            f("services_eyebrow", "Small label", "LAPTOP & COMPUTER SERVICE", "text", ""),
            f("services_title", "Heading (black part)", "When something isn't right,", "text", ""),
            f("services_accent", "Heading (purple part)", "bring it in.", "text", ""),
            f("services_subtitle", "Description", "Our technicians diagnose and fix problems quickly, and explain what was wrong in plain language.", "textarea", "")));

        groups.add(g("products-text", "Products heading", "The product cards themselves are managed in the Products list below.",
            f("products_eyebrow", "Small label", "PRODUCTS & ACCESSORIES", "text", ""),
            f("products_title", "Heading (black part)", "Everything for the desk,", "text", ""),
            f("products_accent", "Heading (purple part)", "in one place.", "text", "")));

        groups.add(g("showcase-text", "\"Complete computer setup\" banner (dark section)", "Change the picture in the Images section. Chips are the small rounded labels under the picture.",
            f("showcase_eyebrow", "Small label", "COMPLETE COMPUTER SETUP", "text", ""),
            f("showcase_title", "Heading", "\u201CTechnology, chosen well.\u201D", "text", ""),
            f("showcase_subtitle", "Description", "Carefully selected components, bringing performance, reliability, and everything you need together.", "textarea", ""),
            f("showcase_chips", "Chips (separate with commas)", "Monitor, Desktop tower, Keyboard, Mouse, Printer, VGA cable, LAN cable, Router", "textarea", "")));

        groups.add(g("why-text", "\"Why choose us\" heading", "The four cards are managed in the Why choose us cards list below. Change the picture in the Images section.",
            f("why_eyebrow", "Small label", "WHY CHOOSE US", "text", ""),
            f("why_title_start", "Heading (first part)", "Built around", "text", ""),
            f("why_accent", "Heading (purple part)", "your", "text", ""),
            f("why_title_end", "Heading (last part)", "whole setup", "text", "")));

        groups.add(g("about-text", "About us section", "",
            f("about_eyebrow", "Small label", "ABOUT US", "text", ""),
            f("about_title", "Heading (black part)", "Your Trusted Partner for", "text", ""),
            f("about_accent", "Heading (purple part)", "Computer Solutions", "text", ""),
            f("about_text", "About paragraph", "Tech G is a computer sales and service business for homes, students and offices. We keep things simple: honest advice, quality products, fair prices and support you can count on after you buy.", "textarea", ""),
            f("pillar1_title", "Box 1 title", "Computer Sales", "text", ""),
            f("pillar1_desc", "Box 1 text", "Laptops and desktops for every need and budget.", "text", ""),
            f("pillar2_title", "Box 2 title", "Service Support", "text", ""),
            f("pillar2_desc", "Box 2 text", "Repairs, upgrades and maintenance with a 1-year service warranty.", "text", ""),
            f("pillar3_title", "Box 3 title", "Accessories & Peripherals", "text", ""),
            f("pillar3_desc", "Box 3 text", "Keyboards, mice, printers and cables in one place.", "text", ""),
            f("pillar4_title", "Box 4 title", "Quality Products", "text", ""),
            f("pillar4_desc", "Box 4 text", "Reliable products we are happy to stand behind.", "text", "")));

        groups.add(g("reviews-text", "Customer reviews heading", "The reviews themselves come from visitors; delete unwanted ones in the Reviews list below.",
            f("reviews_eyebrow", "Small label", "TESTIMONIALS", "text", ""),
            f("reviews_title", "Heading (black part)", "What our customers", "text", ""),
            f("reviews_accent", "Heading (purple part)", "say.", "text", "")));

        groups.add(g("contact-text", "Enquiry section heading", "",
            f("contact_eyebrow", "Small label", "CONTACT", "text", ""),
            f("contact_title", "Heading (black part)", "Send us an", "text", ""),
            f("contact_accent", "Heading (purple part)", "enquiry.", "text", ""),
            f("contact_lead", "Description", "Tell us what you're looking for \u2014 a product, an accessory or a service \u2014 and we'll get back to you.", "textarea", "")));

        imageSlots.add(new ImageSlot("hero_image", "Hero background (top of page)", "Wide landscape, about 1600 x 900 px or larger. Keep the main objects on the right half.", "/images/hero.png"));
        imageSlots.add(new ImageSlot("showcase_image", "\"Complete computer setup\" picture", "Wide landscape photo, about 1600 x 800 px.", "/images/showcase.png"));
        imageSlots.add(new ImageSlot("services_image", "Repair & services banner", "Wide photo of computer repair, about 1600 x 700 px.", "/images/showcase.png"));
        imageSlots.add(new ImageSlot("why_image", "\"Why choose us\" picture", "Square or slightly wide picture, about 1000 x 800 px.", "/images/why-choose-us.png"));

        groups.forEach(gr -> gr.fields().forEach(fl -> fieldByKey.put(fl.key(), fl)));
    }

    private static Field f(String key, String label, String def, String type, String hint) {
        return new Field(key, label, def, type, hint);
    }

    private static Group g(String id, String title, String hint, Field... fields) {
        return new Group(id, title, hint, List.of(fields));
    }

    public List<Group> groups() { return groups; }
    public List<ImageSlot> imageSlots() { return imageSlots; }
    public boolean groupExists(String id) { return groups.stream().anyMatch(x -> x.id().equals(id)); }
    public boolean isImageKey(String key) { return imageSlots.stream().anyMatch(x -> x.key().equals(key)); }

    /** Every editable text: the saved value if there is one, otherwise the default. */
    public Map<String, String> all() {
        Map<String, String> out = new LinkedHashMap<>();
        fieldByKey.forEach((k, fl) -> out.put(k, fl.def()));
        for (SiteSetting s : repo.findAllById(fieldByKey.keySet())) {
            out.put(s.getKey(), s.getValue() == null ? "" : s.getValue());
        }
        return out;
    }

    public String get(String key) { return all().getOrDefault(key, ""); }

    /** Current picture address for each image slot (saved one or the default file). */
    public Map<String, String> images() {
        Map<String, String> out = new LinkedHashMap<>();
        for (ImageSlot slot : imageSlots) {
            String fallback = slot.key().equals("services_image")
                    ? out.getOrDefault("showcase_image", slot.def())
                    : slot.def();
            String v = repo.findById(slot.key()).map(SiteSetting::getValue).filter(x -> x != null && !x.isBlank()).orElse(fallback);
            out.put(slot.key(), v);
        }
        return out;
    }

    public void saveImage(String key, String url) {
        if (!isImageKey(key)) throw new IllegalArgumentException("Unknown image.");
        repo.save(new SiteSetting(key, url));
    }

    public void resetImage(String key) {
        if (!isImageKey(key)) throw new IllegalArgumentException("Unknown image.");
        repo.deleteById(key);
    }

    public boolean hasSocials(Map<String, String> s) {
        return s.entrySet().stream().anyMatch(e -> e.getKey().startsWith("social_") && !e.getValue().isBlank());
    }

    /** Saves only known setting names. Throws IllegalArgumentException with a friendly message if something is invalid. */
    public void save(Map<String, String> params) {
        Map<String, String> clean = new LinkedHashMap<>();
        for (Field fl : fieldByKey.values()) {
            if (!params.containsKey(fl.key())) continue;
            String v = params.get(fl.key()) == null ? "" : params.get(fl.key()).trim();
            if (v.length() > 1000) v = v.substring(0, 1000);
            switch (fl.type()) {
                case "url" -> { if (!v.isEmpty()) v = cleanUrl(v, fl.label()); }
                case "email" -> {
                    if (!v.isEmpty() && !EMAIL.matcher(v).matches())
                        throw new IllegalArgumentException("\"" + v + "\" is not a valid email address.");
                }
                case "whatsapp" -> v = v.replaceAll("[^0-9]", "");
                default -> { }
            }
            clean.put(fl.key(), v);
        }
        clean.forEach((k, v) -> repo.save(new SiteSetting(k, v)));
    }

    private String cleanUrl(String v, String name) {
        String candidate = v.matches("(?i)^https?://.*") ? v : "https://" + v;
        try {
            URI u = new URI(candidate);
            if (u.getHost() == null) throw new IllegalArgumentException(name + " link is not a valid web address.");
            return candidate;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(name + " link is not a valid web address.");
        }
    }
}
