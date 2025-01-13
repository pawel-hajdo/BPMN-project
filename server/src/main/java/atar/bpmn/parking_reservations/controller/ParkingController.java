package atar.bpmn.parking_reservations.controller;

import atar.bpmn.parking_reservations.DTO.ParkingWithSpotsResponse;
import atar.bpmn.parking_reservations.DTO.PaymentBodyTemplate;
import atar.bpmn.parking_reservations.DTO.ReservationRequest;
import atar.bpmn.parking_reservations.DTO.ReservationResponse;
import atar.bpmn.parking_reservations.service.EmailService;
import atar.bpmn.parking_reservations.service.EmitterService;
import atar.bpmn.parking_reservations.service.ParkingService;
import atar.bpmn.parking_reservations.service.ReservationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/api")
public class ParkingController {

    private final ParkingService parkingService;
    private final ReservationService reservationService;
    private final EmitterService emitterService;
    private final EmailService emailService;

    public ParkingController(ParkingService parkingService, ReservationService reservationService, EmitterService emitterService, EmailService emailService) {
        this.parkingService = parkingService;
        this.reservationService = reservationService;
        this.emitterService = emitterService;
        this.emailService = emailService;
    }

    @GetMapping("/parkings")
    public ResponseEntity<List<ParkingWithSpotsResponse>> getAllParkings() {
        List<ParkingWithSpotsResponse> parkingList = parkingService.findAllParkings();
        return ResponseEntity.ok(parkingList);
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String processInstanceKey) {
        return emitterService.addListener(processInstanceKey);
    }

    @PostMapping("/sendReservationRegistered")
    public String sendReservationRegisteredEmail(@RequestParam String to) {
        emailService.sendReservationRegisteredEmail(to);
        return "Reservation registration email sent to: " + to;
    }
}
