package com.reservaction.payment_service.controller;

import com.reservaction.payment_service.client.EventServiceClient;
import com.reservaction.payment_service.client.MailingServiceClient;
import com.reservaction.payment_service.client.ReservationServiceClient;
import com.reservaction.payment_service.dto.PaymentResponse;
import com.reservaction.payment_service.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ReservationServiceClient reservationServiceClient;
    @Autowired
    private MailingServiceClient mailingServiceClient;
    @Autowired
    private EventServiceClient eventServiceClient;

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

            // Send email notification //
            String eventName = session.getMetadata().get("eventName");
            String attendeeEmail = session.getCustomerDetails().getEmail();
            String attendeeName = session.getCustomerDetails().getName();
            String subject = "Reservation Confirmation";
            String body = String.format("Dear %s,\n\n"
                            + "Your reservation with ID %d for %s has been successfully confirmed.\n"
                            + "Payment Amount: $%.2f\n"
                            + "Payment Status: Successful\n\n"
                            + "Thank you for your reservation!\n\n"
                            + "Best Regards,\nReservAction Team",
                    attendeeName, reservationId, eventName, session.getAmountTotal() / 100.0);

            MailingServiceClient.EmailReservation emailDetails = new MailingServiceClient.EmailReservation();
            emailDetails.setEmail(attendeeEmail);
            emailDetails.setSubject(subject);
            emailDetails.setBody(body);

            mailingServiceClient.sendEmail(emailDetails);

            return ResponseEntity.ok("Payment successful, reservation completed, and email sent.");
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process payment success: " + e.getMessage());
        }
    }




}