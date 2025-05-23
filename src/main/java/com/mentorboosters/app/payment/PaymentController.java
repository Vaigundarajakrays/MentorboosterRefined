package com.mentorboosters.app.payment;

import com.mentorboosters.app.response.CommonResponse;
import com.stripe.exception.StripeException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentorboosters/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){this.paymentService=paymentService;}

    @PostMapping("/getClientSecret")
    public CommonResponse<PaymentResponse> getClientSecret(@RequestBody PaymentRequest paymentRequest) throws StripeException {
        return paymentService.getClientSecret(paymentRequest);
    }
}
