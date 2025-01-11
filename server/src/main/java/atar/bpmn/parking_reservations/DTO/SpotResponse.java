package atar.bpmn.parking_reservations.DTO;

public record SpotResponse(
        Long id,
        String spaceCode,
        Long spotTypeId
) {}
