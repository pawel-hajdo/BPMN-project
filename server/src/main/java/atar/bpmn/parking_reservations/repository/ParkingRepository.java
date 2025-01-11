package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {
}
