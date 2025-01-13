package com.reservaction.payment_service.service;

import com.reservaction.payment_service.client.ReservationServiceClient;
import com.reservaction.payment_service.dto.PaymentResponse;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    @Autowired
    private ReservationServiceClient reservationServiceClient;

    public PaymentResponse checkout(Long reservationId) {

        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";

        // Get reservation details from reservation service
        ReservationServiceClient.ReservationResponse reservation =
                reservationServiceClient.getReservationDetails(reservationId);

        if (reservation.getStatus() != ReservationServiceClient.ReservationStatus.PENDING) {
            throw new RuntimeException("Invalid reservation status for payment");
        }

        Session session = null;
        try {
            // Build the session parameters
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Ticket Reservation #" + reservation.getReservationId())
                    .setDescription(reservation.getEventTitle() + " - " + reservation.getNumberOfTickets() + " tickets")
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("USD")
                    .setUnitAmount((long) (reservation.getTicketUnitPrice() * 100))
                    .setProductData(productData)
                    .build();

            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity((long) reservation.getNumberOfTickets())
                    .setPriceData(priceData)
                    .build();

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:9090/success")
                    .setCancelUrl("http://localhost:9090/cancel")
                    .addLineItem(lineItem)
                    .build();

            // Attempt to create the session
            session = Session.create(params);

        } catch (StripeException ex) {
            throw new RuntimeException("Failed to create Stripe session: " + ex.getMessage(), ex);
        }

        // Ensure the session is not null before proceeding
        if (session == null) {
            throw new RuntimeException("Stripe session creation returned null");
        }

        return PaymentResponse.builder()
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .reservationId(reservation.getReservationId())
                .amount(reservation.getTotalPrice())
                .build();
    }
}


