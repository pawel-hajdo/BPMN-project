package atar.bpmn.parking_reservations.service;

import atar.bpmn.parking_reservations.model.PriceRate;
import atar.bpmn.parking_reservations.model.Reservation;
import atar.bpmn.parking_reservations.model.ReservationStatus;
import atar.bpmn.parking_reservations.model.Spot;
import atar.bpmn.parking_reservations.repository.PaymentRepository;
import atar.bpmn.parking_reservations.repository.PriceRateRepository;
import atar.bpmn.parking_reservations.repository.ReservationRepository;
import atar.bpmn.parking_reservations.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class ReservationService {
    private final SpotRepository spotRepository;
    private final ReservationRepository reservationRepository;
    private final PriceRateRepository priceRateRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(SpotRepository spotRepository, ReservationRepository reservationRepository,
            PriceRateRepository priceRateRepository, PaymentRepository paymentRepository) {
        this.spotRepository = spotRepository;
        this.reservationRepository = reservationRepository;
        this.priceRateRepository = priceRateRepository;
        this.paymentRepository = paymentRepository;
    }

    public boolean checkIfSpotAvailable(Long spotId, LocalDateTime startTime, LocalDateTime enDateTime) {
        List<Reservation> overlappingReservations = reservationRepository.findReservationsBetweenDates(spotId,
                startTime, enDateTime);
        System.out.println(overlappingReservations);

        return overlappingReservations.isEmpty();
    }

    public Long createReservation(Long spotId, LocalDateTime startTime, LocalDateTime endTime,
            PriceRate applicableRate) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Spot spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new IllegalArgumentException("Spot not found"));

        boolean isSpotFree = checkIfSpotAvailable(spotId, startTime, endTime);
        if (!isSpotFree) {
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

    public String finalizeReservation(Long reservationId, Long paymentId) {

        String code = generateCode();
        Reservation reservationToUpdate = reservationRepository.findById(reservationId).orElseThrow();

        reservationToUpdate.setAccessCode(code);
        reservationToUpdate.setStatus(ReservationStatus.CONFIRMED);
        reservationToUpdate.setPayment(
            paymentRepository.findById(paymentId).orElseThrow()
        );

        reservationRepository.save(reservationToUpdate);
        return code;
    }

    private String generateCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public void cancelReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }
}
