package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
