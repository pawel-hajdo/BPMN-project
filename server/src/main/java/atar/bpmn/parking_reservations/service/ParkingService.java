package atar.bpmn.parking_reservations.service;

import atar.bpmn.parking_reservations.DTO.ParkingWithSpotsResponse;
import atar.bpmn.parking_reservations.DTO.SpotResponse;
import atar.bpmn.parking_reservations.model.Parking;
import atar.bpmn.parking_reservations.repository.ParkingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingService {

    private final ParkingRepository parkingRepository;

    public ParkingService(ParkingRepository parkingRepository) {
        this.parkingRepository = parkingRepository;
    }

    public List<ParkingWithSpotsResponse> findAllParkings() {
        List<Parking> parkingList = parkingRepository.findAll();

        return parkingList.stream()
                .map(parking -> new ParkingWithSpotsResponse(
                        parking.getId(),
                        parking.getCity(),
                        parking.getStreet(),
                        parking.getAddress(),
                        mapSpotsToResponse(parking)
                ))
                .collect(Collectors.toList());
    }

    private List<SpotResponse> mapSpotsToResponse(Parking parking) {
        return parking.getSpots().stream()
                .map(spot -> new SpotResponse(
                        spot.getId(),
                        spot.getSpaceCode(),
                        spot.getSpotType().getId()
                ))
                .collect(Collectors.toList());
    }

}
