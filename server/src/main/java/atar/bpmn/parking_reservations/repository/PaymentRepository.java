package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
