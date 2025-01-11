package atar.bpmn.parking_reservations.DTO;

import java.util.List;

public record ParkingWithSpotsResponse(
        Long id,
        String city,
        String street,
        String address,
        List<SpotResponse> spots
) {}
