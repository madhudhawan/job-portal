package com.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // ==============================
    // ACCEPT EMAIL
    // ==============================
    public void sendAcceptanceEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName) {

        System.out.println("🔥 EMAIL METHOD CALLED - ACCEPT");
        System.out.println("📧 Sending to: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("🎉 Selected for " + jobTitle);

        message.setText(
                "Dear " + candidateName + ",\n\n" +
                "Congratulations! 🎉\n\n" +
                "You are selected for " + jobTitle + " at " + companyName + ".\n\n" +
                "We will contact you soon.\n\n" +
                "Regards,\n" +
                companyName + " Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ ACCEPT EMAIL SENT");
        } catch (Exception e) {
            System.out.println("❌ ACCEPT EMAIL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==============================
    // REJECT EMAIL
    // ==============================
    public void sendRejectionEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName) {

        System.out.println("🔥 EMAIL METHOD CALLED - REJECT");
        System.out.println("📧 Sending to: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Application Update - " + jobTitle);

        message.setText(
                "Dear " + candidateName + ",\n\n" +
                "Thank you for applying for " + jobTitle + " at " + companyName + ".\n\n" +
                "We regret to inform you that you are not selected.\n\n" +
                "Best wishes for future.\n\n" +
                "Regards,\n" +
                companyName + " Team"
        );

        try {
            mailSender.send(message);
            System.out.println("❌ REJECT EMAIL SENT");
        } catch (Exception e) {
            System.out.println("❌ REJECT EMAIL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==============================
    // APPLY EMAIL
    // ==============================
    public void sendApplicationConfirmationEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName,
            double matchScore) {

        System.out.println("🔥 EMAIL METHOD CALLED - APPLY");
        System.out.println("📧 Sending to: " + toEmail);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Application Received - " + jobTitle);

        message.setText(
                "Dear " + candidateName + ",\n\n" +
                "Your application has been successfully submitted! ✅\n\n" +
                "Position: " + jobTitle + "\n" +
                "Company: " + companyName + "\n\n" +
                "We will review and get back to you.\n\n" +
                "Regards,\nJob Portal Team"
        );

        try {
            mailSender.send(message);
            System.out.println("✅ APPLY EMAIL SENT");
        } catch (Exception e) {
            System.out.println("❌ APPLY EMAIL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}