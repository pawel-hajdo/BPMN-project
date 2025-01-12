package atar.bpmn.parking_reservations.service;

import atar.bpmn.parking_reservations.DTO.ReservationResponse;
import atar.bpmn.parking_reservations.model.PriceRate;
import atar.bpmn.parking_reservations.model.Reservation;
import atar.bpmn.parking_reservations.model.ReservationStatus;
import atar.bpmn.parking_reservations.model.Spot;
import atar.bpmn.parking_reservations.repository.PriceRateRepository;
import atar.bpmn.parking_reservations.repository.ReservationRepository;
import atar.bpmn.parking_reservations.repository.SpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {
    private final SpotRepository spotRepository;
    private final ReservationRepository reservationRepository;
    private final PriceRateRepository priceRateRepository;

    public ReservationService(SpotRepository spotRepository, ReservationRepository reservationRepository, PriceRateRepository priceRateRepository) {
        this.spotRepository = spotRepository;
        this.reservationRepository = reservationRepository;
        this.priceRateRepository = priceRateRepository;
    }

    public boolean checkIfSpotAvailable(Long spotId, LocalDateTime startTime, LocalDateTime enDateTime) {
        List<Reservation> overlappingReservations = reservationRepository.findReservationsBetweenDates(spotId, startTime, enDateTime);
        System.out.println(overlappingReservations);

        return overlappingReservations.isEmpty();
    }

    public Long createReservation(Long spotId, LocalDateTime startTime, LocalDateTime endTime, PriceRate applicableRate){
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("Spot not found"));

        boolean spotAlreadyReserved = reservationRepository.existsBySpotIdAndStatus(spotId, ReservationStatus.PENDING);
        if (spotAlreadyReserved) {
            throw new IllegalStateException("Spot is already reserved.");
        }

        Reservation reservation = new Reservation();
        reservation.setSpot(spot);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setPriceRate(applicableRate);
        reservation.setStatus(ReservationStatus.PENDING);

        reservationRepository.save(reservation);

        return reservation.getId();
    }

    public Map<String, Object> calculateTotalCost(LocalDateTime startTime, LocalDateTime endTime) {
        long hours = Duration.between(startTime, endTime).toMinutes() / 60;
        if (Duration.between(startTime, endTime).toMinutes() % 60 != 0) {
            hours++;
        }

        if (hours < 1) {
            throw new IllegalArgumentException("Reservation must be at least 1 hour");
        }

        List<PriceRate> priceRates = priceRateRepository.findAll();
        PriceRate applicableRate = findApplicableRate(priceRates, hours);
        Long totalCost = hours * applicableRate.getCostPerStandardHour();

        Map<String, Object> result = new HashMap<>();
        result.put("priceRate", applicableRate);
        result.put("totalCost", totalCost);

        return result;
    }

    private PriceRate findApplicableRate(List<PriceRate> priceRates, long hours) {
        if (priceRates.isEmpty()) {
            throw new IllegalStateException("No price rates defined in the system");
        }

        return priceRates.stream()
                .sorted(Comparator.comparing(PriceRate::getMinHours).reversed())
                .filter(rate -> hours >= rate.getMinHours())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No applicable price rate found for " + hours + " hours"));
    }
}
