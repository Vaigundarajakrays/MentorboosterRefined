//package com.mentorboosters.app.zoom;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//
//
//@Component
//public class SecretCheck {
//
//    @Value("${stripe.webhook.secret}")
//    private String stripeApiKey;
//
//    @PostConstruct
//    public void showSecret() {
//        System.out.println("🔥 Stripe API Key from AWS Secrets Manager: " + stripeApiKey);
//    }
//}
