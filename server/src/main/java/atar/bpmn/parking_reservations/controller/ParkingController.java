package atar.bpmn.parking_reservations.controller;

import atar.bpmn.parking_reservations.DTO.ParkingWithSpotsResponse;
import atar.bpmn.parking_reservations.DTO.ReservationRequest;
import atar.bpmn.parking_reservations.DTO.ReservationResponse;
import atar.bpmn.parking_reservations.service.ParkingService;
import atar.bpmn.parking_reservations.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ParkingController {

    private final ParkingService parkingService;
    private final ReservationService reservationService;

    public ParkingController(ParkingService parkingService, ReservationService reservationService) {
        this.parkingService = parkingService;
        this.reservationService = reservationService;
    }

    @GetMapping("/parkings")
    public ResponseEntity<List<ParkingWithSpotsResponse>> getAllParkings() {
        List<ParkingWithSpotsResponse> parkingList = parkingService.findAllParkings();
        return ResponseEntity.ok(parkingList);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request.spotId(), request.startTime(), request.endTime());

        return ResponseEntity.ok(response);
    }
}
