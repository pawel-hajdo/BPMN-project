package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.PriceRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRateRepository extends JpaRepository<PriceRate, Long> {
}
