package atar.bpmn.parking_reservations.DTO;

import atar.bpmn.parking_reservations.model.Card;

public record PaymentBodyTemplate(
    Card card,
    Long reservationId,
    String mail
) {}
