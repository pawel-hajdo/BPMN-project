package atar.bpmn.parking_reservations.repository;

import atar.bpmn.parking_reservations.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

import atar.bpmn.parking_reservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
    SELECT r 
    FROM Reservation r
    WHERE r.spot.id = :spotId 
      AND r.startTime < :endTime 
      AND r.endTime > :startTime
    """)
    List<Reservation> findReservationsBetweenDates(
        @Param("spotId") Long spotId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    boolean existsBySpotIdAndStatus(Long spotId, ReservationStatus status);
}
