package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
