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
import java.util.List;

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

    @Transactional
    public ReservationResponse createReservation(Long spotId, LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("Spot not found"));


        System.out.println(startTime+" " +" "+ endTime);
        long hours = Duration.between(startTime, endTime).toMinutes() / 60;
        if (Duration.between(startTime, endTime).toMinutes() % 60 != 0) {
            hours++;
        }

        if (hours < 1) {
            throw new IllegalArgumentException("Reservation must be at least 1 hour");
        }

        List<PriceRate> priceRates = priceRateRepository.findAll();
        PriceRate applicableRate = findApplicableRate(priceRates, hours);

        long totalCost = hours * applicableRate.getCostPerStandardHour();

        Reservation reservation = new Reservation();
        reservation.setSpot(spot);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setPriceRate(applicableRate);
        reservation.setStatus(ReservationStatus.PENDING);

        reservationRepository.save(reservation);

        return new ReservationResponse(reservation.getId(), totalCost);
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
