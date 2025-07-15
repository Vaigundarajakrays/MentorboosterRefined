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

}
