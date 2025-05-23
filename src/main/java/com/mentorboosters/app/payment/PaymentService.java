package com.mentorboosters.app.payment;

import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.util.Constant;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.mentorboosters.app.util.Constant.*;

@Service
public class PaymentService {

    @Value("${stripe.apikey}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public CommonResponse<PaymentResponse> getClientSecret(PaymentRequest paymentRequest) throws StripeException {

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())  // Amount in cents
                .setCurrency(paymentRequest.getCurrency())  // "cad" for Canada
                .addPaymentMethodType("card")
                .build();


        PaymentIntent paymentIntent = PaymentIntent.create(params);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(paymentIntent.getId())
                .status(paymentIntent.getStatus())
                .clientSecret(paymentIntent.getClientSecret())
                .build();

        return CommonResponse.<PaymentResponse>builder()
                .message(LOADED_CLIENT_SECRET)
                .status(STATUS_TRUE)
                .data(paymentResponse)
                .statusCode(SUCCESS_CODE)
                .build();


    }
}
