package com.mentorboosters.app.payment;

import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.model.Booking;
import com.mentorboosters.app.response.CommonResponse;
import com.stripe.exception.StripeException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mentorboosters/api")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){this.paymentService=paymentService;}

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MENTOR')")
    @PostMapping("/checkout")
    public CommonResponse<PaymentResponse> checkoutProducts(@RequestBody Booking booking) throws StripeException, UnexpectedServerException, ResourceNotFoundException {
        return paymentService.checkoutProducts(booking);
    }
}
