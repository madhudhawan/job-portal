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

    // Gets your gmail from application.properties automatically
    @Value("${spring.mail.username}")
    private String fromEmail;

    // ==============================
    // Send email when candidate is ACCEPTED
    // ==============================
    public void sendAcceptanceEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
            "🎉 Congratulations! Selected for " + jobTitle + " at " + companyName
        );
        message.setText(
            "Dear " + candidateName + ",\n\n" +
            "Congratulations! 🎉\n\n" +
            "We are pleased to inform you that you have been SELECTED " +
            "for the position of " + jobTitle + " at " + companyName + ".\n\n" +
            "Our recruitment team will contact you shortly with " +
            "further details about the next steps.\n\n" +
            "Best regards,\n" +
            companyName + " Recruitment Team\n\n" +
            "---\n" +
            "This is an automated email from Job Portal."
        );

        mailSender.send(message);
    }

    // ==============================
    // Send email when candidate is REJECTED
    // ==============================
    public void sendRejectionEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
            "Application Update — " + jobTitle + " at " + companyName
        );
        message.setText(
            "Dear " + candidateName + ",\n\n" +
            "Thank you for applying for the position of " +
            jobTitle + " at " + companyName + ".\n\n" +
            "After careful consideration, we regret to inform you " +
            "that we will not be moving forward with your application " +
            "at this time.\n\n" +
            "We appreciate your interest and encourage you to apply " +
            "for future openings that match your skills.\n\n" +
            "Best regards,\n" +
            companyName + " Recruitment Team\n\n" +
            "---\n" +
            "This is an automated email from Job Portal."
        );

        mailSender.send(message);
    }

    // ==============================
    // Send email when candidate successfully applies
    // ==============================
    public void sendApplicationConfirmationEmail(
            String toEmail,
            String candidateName,
            String jobTitle,
            String companyName,
            double matchScore) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(
            "Application Received — " + jobTitle + " at " + companyName
        );
//        message.setText(
//            "Dear " + candidateName + ",\n\n" +
//            "Your application has been successfully submitted! ✅\n\n" +
//            "Position  : " + jobTitle + "\n" +
//            "Company   : " + companyName + "\n" +
//            "Our recruitment team will review your application " +
//            "and get back to you soon.\n\n" +
//            "Best regards,\n" +
//            "Job Portal Team\n\n" +
//            "---\n" +
//            "This is an automated email from Job Portal."
//        );
//
//        mailSender.send(message);
        message.setText(
        	    "Dear " + candidateName + ",\n\n" +
        	    "Your application has been successfully submitted! ✅\n\n" +
        	    "Position  : " + jobTitle + "\n" +
        	    "Company   : " + companyName + "\n\n" +
        	    // ← score line removed completely
        	    "Our recruitment team will review your " +
        	    "application and get back to you soon.\n\n" +
        	    "Best regards,\n" +
        	    "Job Portal Team\n\n" +
        	    "---\n" +
        	    "This is an automated email from Job Portal."
        	);
        mailSender.send(message);
    }
}