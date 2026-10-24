package com.techg.service;

import com.techg.entity.QuoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String fromAddress;

    private final SettingsService settings;

    public EmailService(JavaMailSender mailSender, SettingsService settings) {
        this.mailSender = mailSender;
        this.settings = settings;
    }

    /** Sends the admin an email about a new enquiry. Never throws: a mail problem must not lose the enquiry. */
    public void sendEnquiryNotification(QuoteRequest q) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
            String to = settings.get("notify_email");
            if (to == null || to.isBlank()) {
                log.warn("No notification email set in the admin panel; enquiry was saved but no email was sent.");
                return;
            }
            mail.setTo(to);
            mail.setSubject("New Tech G enquiry: " + q.getEnquiryType() + " from " + q.getName());
            mail.setText(
                "A new enquiry was submitted on the Tech G website.\n\n"
                + "Type: " + q.getEnquiryType() + "\n"
                + "Name: " + q.getName() + "\n"
                + "Phone: " + q.getPhone() + "\n"
                + "Product / Service: " + q.getProductName() + "\n"
                + "Quantity: " + q.getQuantity() + "\n"
                + "Specifications: " + nz(q.getSpecifications()) + "\n"
                + "Message: " + nz(q.getMessage()) + "\n"
                + "Received: " + q.getCreatedAt() + "\n");
            mailSender.send(mail);
        } catch (Exception e) {
            log.warn("Could not send enquiry email (enquiry was still saved): {}", e.getMessage());
        }
    }

    private String nz(String s) { return (s == null || s.isBlank()) ? "-" : s; }
}
