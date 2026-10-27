package com.techg.service;

import com.techg.entity.QuoteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // Example: 27 Oct 2026, 07:03 AM
    private static final DateTimeFormatter WHEN =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final JavaMailSender mailSender;
    private final SettingsService settings;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender, SettingsService settings) {
        this.mailSender = mailSender;
        this.settings = settings;
    }

    /** Emails the admin about a new enquiry. Never throws: a mail problem must not lose the enquiry. */
    public void sendEnquiryNotification(QuoteRequest q) {
        // 1) the address typed in the admin panel, 2) otherwise the Gmail account that sends the mails
        String to = settings.get("notify_email");
        if (to == null || to.isBlank()) {
            to = fromAddress;
        }
        if (to == null || to.isBlank()) {
            log.warn("EMAIL NOT SENT: no receiving address. Set 'Email that receives enquiries' in the admin panel. (Enquiry was saved.)");
            return;
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("EMAIL NOT SENT: spring.mail.username is empty. Check application-local.properties and that the 'local' profile is active. (Enquiry was saved.)");
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromAddress);
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
                + "Received: " + when(q.getCreatedAt()) + "\n");
            mailSender.send(mail);
            log.info("EMAIL SENT to {} for enquiry from {}", to, q.getName());
        } catch (Exception e) {
            log.warn("EMAIL FAILED to {} (enquiry was still saved): {}", to, e.getMessage());
        }
    }

    private String nz(String s) { return (s == null || s.isBlank()) ? "-" : s; }

    private String when(LocalDateTime t) { return t == null ? "-" : t.format(WHEN); }
}