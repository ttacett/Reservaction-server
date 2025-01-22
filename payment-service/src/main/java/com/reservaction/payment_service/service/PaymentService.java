package com.reservaction.payment_service.service;


import com.reservaction.payment_service.client.EventServiceClient;
import com.reservaction.payment_service.client.ReservationServiceClient;
import com.reservaction.payment_service.client.UserServiceClient;
import com.reservaction.payment_service.dto.PaymentResponse;
import com.stripe.model.Transfer;
import com.stripe.model.checkout.Session;
import com.stripe.param.TransferCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PaymentService {

    @Autowired
    private ReservationServiceClient reservationServiceClient;
    @Autowired
    private EventServiceClient eventServiceClient;
    @Autowired
    private UserServiceClient userServiceClient;

    public PaymentResponse checkout(Long reservationId) {
        Stripe.apiKey = "sk_test_51QNFpAGyn2SQYaBfihz3dOWtFpwRWtvP4UDSe63TQEiJKwtCrPphBZeoR4jJfD4lboPKabpVSLyxomHbP8vuPdEb00Or2dylJG";

        // Retrieve reservation details from reservation service
        ReservationServiceClient.ReservationResponse reservation =
                reservationServiceClient.getReservationDetails(reservationId);

        if (reservation.getStatus() != ReservationServiceClient.ReservationStatus.PENDING) {
            throw new RuntimeException("Invalid reservation status for payment");
        }

        // Retrieve organizer's ID from event
        String organizerId = eventServiceClient.getOrganizerByEvent(reservation.getEventId());

        // Retrieve organizer's Stripe account ID
        String organizerStripeAccountId = userServiceClient.getStripeAccount(organizerId);

        if (organizerStripeAccountId == null) {
            throw new RuntimeException("Organizer's Stripe account ID not found");
        }

        double totalAmount = reservation.getTotalPrice();
        double commissionAmount = totalAmount * 0.10;
        double organizerAmount = totalAmount - commissionAmount;
        Session session;
        try {
            // Build session params //
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName("Ticket Reservation #" + reservation.getReservationId())
                    .setDescription(reservation.getEventTitle() + " - " + reservation.getNumberOfTickets() + " tickets")
                    .build();

            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("usd")
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
                    .putMetadata("reservationId", reservationId.toString())
                    .putMetadata("organizerStripeAccountId", organizerStripeAccountId)
                    .build();

            session = Session.create(params);

            // Attempt the transfer to the organizer //
            transferToOrganizer(reservation, organizerStripeAccountId, organizerAmount);

        } catch (StripeException ex) {
            throw new RuntimeException("Failed to create Stripe session or transfer: " + ex.getMessage(), ex);
        }

        if (session == null) {
            throw new RuntimeException("Stripe session creation returned null");
        }

        return PaymentResponse.builder()
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .reservationId(reservation.getReservationId())
                .amount(totalAmount)
                .commissionAmount(commissionAmount)
                .organizerAmount(organizerAmount)
                .build();
    }


    // transfer to the organizer //
    private void transferToOrganizer(ReservationServiceClient.ReservationResponse reservation, String organizerStripeAccountId, double organizerAmount) throws StripeException {
        TransferCreateParams transferParams = TransferCreateParams.builder()
                .setAmount((long) (organizerAmount * 100))
                .setCurrency("usd")
                .setDestination(organizerStripeAccountId)
                .setDescription("Fees for reservation #" + reservation.getReservationId())
                .build();

        Transfer.create(transferParams);
    }
}


