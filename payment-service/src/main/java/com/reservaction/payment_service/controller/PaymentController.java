package com.reservaction.payment_service.controller;

import com.reservaction.payment_service.client.ReservationServiceClient;
import com.reservaction.payment_service.dto.PaymentResponse;
import com.reservaction.payment_service.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


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

    @GetMapping("/success")
    public ResponseEntity<?> handleSuccess(@RequestParam("session_id") String sessionId) {
        try {
            // Retrieve Stripe session //
            Session session = Session.retrieve(sessionId);

            // Extract metadata //
            String reservationIdStr = session.getMetadata().get("reservationId");
            if (reservationIdStr == null) {
                return ResponseEntity.badRequest().body("Reservation ID not found in session metadata.");
            }

            Long reservationId = Long.parseLong(reservationIdStr);

            reservationServiceClient.confirmReservation(reservationId);

            return ResponseEntity.ok("Payment successful, reservation completed.");
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process payment success: " + e.getMessage());
        }
    }

//    @GetMapping("/success")
//    public ResponseEntity<?> handleSuccess(@RequestParam("session_id") String sessionId) {
//        try {
//            // Retrieve Stripe session
//            Session session = Session.retrieve(sessionId);
//
//            // Extract metadata
//            String reservationIdStr = session.getMetadata().get("reservationId");
//            if (reservationIdStr == null) {
//                return ResponseEntity.badRequest().body("Reservation ID not found in session metadata.");
//            }
//
//            Long reservationId = Long.parseLong(reservationIdStr);
//
//            // Retrieve the JWT token from the incoming request
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String token = request.getHeader("Authorization");
//
//            if (token == null || !token.startsWith("Bearer ")) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header.");
//            }
//
//            // Call reservation service with the existing token
//            reservationServiceClient.confirmReservation(reservationId, token);
//
//            return ResponseEntity.ok("Payment successful, reservation completed.");
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Failed to process payment success: " + e.getMessage());
//        }
//    }



}