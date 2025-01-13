package com.reservaction.payment_service.controller;

import com.reservaction.payment_service.client.ReservationServiceClient;
import com.reservaction.payment_service.dto.PaymentResponse;
import com.reservaction.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationServiceClient reservationServiceClient;

    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<PaymentResponse> checkout(@PathVariable Long reservationId){
            PaymentResponse response = paymentService.checkout(reservationId);
            return ResponseEntity.ok(response);
    }

/*
    @PostMapping("/create-session/{reservationId}")
    public ResponseEntity<PaymentResponse> createPaymentSession(@PathVariable Long reservationId) {
        try {
            PaymentResponse response = paymentService.createPaymentSession(reservationId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/payment/success")
    public ResponseEntity<?> handlePaymentSuccess(
            @RequestParam("session_id") String sessionId,
            @RequestParam("reservation_id") Long reservationId
    ) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("complete".equals(session.getStatus())) {
                reservationServiceClient.confirmReservation(reservationId);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (StripeException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
*/


}