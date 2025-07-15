package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.BookingDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.payment.PaymentResponse;
import com.mentorboosters.app.payment.PaymentService;
import com.mentorboosters.app.response.CommonResponse;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MENTOR')")
    @PostMapping("/checkout")
    public CommonResponse<PaymentResponse> checkoutProducts(@RequestBody BookingDTO bookingDTO) throws StripeException, UnexpectedServerException, ResourceNotFoundException {
        return paymentService.checkoutProducts(bookingDTO);
    }
}
