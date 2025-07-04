package com.mentorboosters.app.util;

import com.mentorboosters.app.enumUtil.OtpPurpose;
import com.mentorboosters.app.model.Mentor;
import com.mentorboosters.app.model.MentorProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class CommonFiles {


    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String mailFrom;


    public CommonFiles(JavaMailSender mailSender){this.mailSender=mailSender;}

    public String generateAlphaPassword(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        // Generate a password with the specified length
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            password.append(alphabet.charAt(index));
        }
        return password.toString();
    }

    public String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public void sendPasswordToMentor(Mentor mentor, String password) {
        String emailBody = "Dear " + mentor.getName() + ",\n\n" +
                "Welcome to Mentor Boosters! We’re thrilled to have you join us as a mentor and look forward to your valuable contributions to our community.\n\n" +
                "Below are your login credentials for accessing our portal:\n\n" +
                "Email Id : " + mentor.getEmail() + "\n" +
                "Password : " + password + "\n\n" +
                "To log in, please visit: " + "https://www.mentorboosters.com/#/home " + "\n\n" +
                "For your security, we recommend updating your password upon your first login.\n\n" +
                "Thank you for joining our mission, and we look forward to working with you!\n\n" +
                "Best regards,\nMentor Boosters Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mentor.getEmail());
        message.setSubject("Welcome to MentorBoosters - Your Login Details");
        message.setText(emailBody);
        message.setFrom("admin@mentorboosters.com");
        mailSender.send(message);
    }

    public void sendPasswordToMentorNew(MentorProfile mentor, String password) {
        String emailBody = "Dear " + mentor.getName() + ",\n\n" +
                "Welcome to Mentor Boosters! We’re thrilled to have you join us as a mentor and look forward to your valuable contributions to our community.\n\n" +
                "Below are your login credentials for accessing our portal:\n\n" +
                "Email Id : " + mentor.getEmail() + "\n" +
                "Password : " + password + "\n\n" +
                "To log in, please visit: " + "https://www.mentorboosters.com/#/home " + "\n\n" +
                "For your security, we recommend updating your password upon your first login.\n\n" +
                "Thank you for joining our mission, and we look forward to working with you!\n\n" +
                "Best regards,\nMentor Boosters Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mentor.getEmail());
        message.setSubject("Welcome to MentorBoosters - Your Login Details");
        message.setText(emailBody);
        message.setFrom("vaigundaraja.krays@gmail.com");
        mailSender.send(message);
    }

    public void sendOTPUser(String email, String otp, OtpPurpose purpose) {
        String subject;
        String body;

        switch (purpose) {
            case MENTOR_REGISTER -> {
                subject = "Mentor Registration - Verify Your Email";
                body = "Dear Mentor,\n\n" +
                        "Thank you for signing up as a mentor at Mentor Boosters! Please verify your email address using the One-Time Password (OTP) below:\n\n" +
                        "OTP: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Warm regards,\nThe Mentor Boosters Team";
            }
            case MENTEE_REGISTER -> {
                subject = "Mentee Registration - Verify Your Email";
                body = "Dear Mentee,\n\n" +
                        "Thanks for joining Mentor Boosters! Please verify your email with the OTP below:\n\n" +
                        "OTP: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Warm regards,\nThe Mentor Boosters Team";
            }
            case FORGOT_PASSWORD -> {
                subject = "Reset Your Password";
                body = "Hey there,\n\n" +
                        "You requested a password reset for your Mentor Boosters account. Use the OTP below to proceed:\n\n" +
                        "OTP: " + otp + "\n\n" +
                        "The OTP will expire in 5 minutes.\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Warm regards,\nThe Mentor Boosters Team";
            }
            default -> {
                subject = "Mentor Boosters OTP";
                body = "Your OTP is: " + otp;
            }
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(mailFrom);
        mailSender.send(message);
    }


    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@example.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
