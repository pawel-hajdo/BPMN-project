package atar.bpmn.parking_reservations.controller;

import atar.bpmn.parking_reservations.DTO.ParkingWithSpotsResponse;
import atar.bpmn.parking_reservations.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @GetMapping("/parkings")
    public ResponseEntity<List<ParkingWithSpotsResponse>> getAllParkings() {
        List<ParkingWithSpotsResponse> parkingList = parkingService.findAllParkings();
        return ResponseEntity.ok(parkingList);
    }
}
