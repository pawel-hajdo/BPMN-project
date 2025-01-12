package atar.bpmn.parking_reservations.DTO;

import java.time.LocalDateTime;

public record ReservationRequest(
        Long spotId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
