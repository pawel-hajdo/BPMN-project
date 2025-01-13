package atar.bpmn.parking_reservations.worker;

import atar.bpmn.parking_reservations.model.PriceRate;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import atar.bpmn.parking_reservations.service.EmitterService;
import atar.bpmn.parking_reservations.service.ReservationService;

import java.time.LocalDateTime;
import java.util.Map;
import org.json.JSONObject;

@Component
@AllArgsConstructor
public class ReservationWorker {

    private final ReservationService reservationService;
    private final EmitterService emitterService;

    @JobWorker(type = "check_for_spot")
    public Map<String, Object> checkForSpot(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("check for spot");
        System.out.println(jobResultVariables);

        Long id = ((Number) jobResultVariables.get("spotId")).longValue();
        LocalDateTime start = LocalDateTime.parse(jobResultVariables.get("startTime").toString());
        LocalDateTime end = LocalDateTime.parse(jobResultVariables.get("endTime").toString());

        boolean isFree = reservationService.checkIfSpotAvailable(id, start, end);
        jobResultVariables.put("isFree",isFree);

        return jobResultVariables;
    }

    @JobWorker(type = "send_no_space_message")
    public Map<String, Object> sendNoSpaceMessage(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_no_space_message");

        boolean isFree = (boolean) jobResultVariables.get("isFree");

        JSONObject eventMessage = new JSONObject();
        eventMessage.put("isSpaceAvaliable", isFree);
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);   

        return jobResultVariables;
    }

    @JobWorker(type = "send_spot_avaliavle_message")
    public Map<String, Object> sendSpotAvaliableMessage(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_no_space_message");
        boolean isFree = (boolean) jobResultVariables.get("isFree");

        JSONObject eventMessage = new JSONObject();
        eventMessage.put("isSpaceAvaliable", isFree);
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);   

        return jobResultVariables;
    }    

    @JobWorker(type = "prepare_price")
    public Map<String, Object> preparePrice(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("prepare_price");
        System.out.println(jobResultVariables);

        LocalDateTime startTime = LocalDateTime.parse(jobResultVariables.get("startTime").toString());
        LocalDateTime endTime = LocalDateTime.parse(jobResultVariables.get("endTime").toString());

        Map<String, Object> costResult = reservationService.calculateTotalCost(startTime, endTime);

        JSONObject eventMessage = new JSONObject(costResult);
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);

        jobResultVariables.putAll(costResult);
        return jobResultVariables;
    }

    @JobWorker(type = "prepare_reservation")
    public Map<String, Object> prepareReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("prepare_reservation");
        System.out.println(jobResultVariables);

        Long spotId = ((Number) jobResultVariables.get("spotId")).longValue();
        LocalDateTime startTime = LocalDateTime.parse(jobResultVariables.get("startTime").toString());
        LocalDateTime endTime = LocalDateTime.parse(jobResultVariables.get("endTime").toString());

        if (!(Boolean) jobResultVariables.get("isFree")) {
            throw new IllegalStateException("Spot is not available for reservation");
        }

        PriceRate priceRate = new PriceRate();
        priceRate.setId(((Number) ((Map<String, Object>) jobResultVariables.get("priceRate")).get("id")).longValue());

        Long reservationId = reservationService.createReservation(spotId, startTime, endTime, priceRate);

        JSONObject eventMessage = new JSONObject();
        eventMessage.put("reservationId", reservationId);
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);

        jobResultVariables.put("reservationId", reservationId);
        return jobResultVariables;
    }

    @JobWorker(type = "send_ticket")
    public Map<String, Object> sendTicket(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_ticket");
        JSONObject eventMessage = new JSONObject();
        eventMessage.put("success", jobResultVariables.get("access_code").toString());
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);

        return jobResultVariables;
    }

    @JobWorker(type = "cancel_reservation")
    public Map<String, Object> cancelReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("cancel_reservation");

        Long reservationId = Long.parseLong(jobResultVariables.get("reservationId").toString());
        
        reservationService.cancelReservation(reservationId);
        return jobResultVariables;
    }

    @JobWorker(type = "finalize_reservation")
    public Map<String, Object> finaleReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();
        
        System.out.println("finalize_reservation");
        Long reservationId = Long.parseLong(jobResultVariables.get("reservationId").toString());
        Long paymentId = Long.parseLong(jobResultVariables.get("paymentId").toString());

        String code = reservationService.finalizeReservation(reservationId, paymentId);
        
        jobResultVariables.put("accessCode", code);
        return jobResultVariables;
    }

    @JobWorker(type = "send_cancellation_message")
    public Map<String, Object> sendCancellationMessage(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_cancellation_message");
        JSONObject eventMessage = new JSONObject();
        eventMessage.put("error", "error");
        emitterService.sendMessageToListener(String.valueOf(job.getProcessInstanceKey()), eventMessage);

        return jobResultVariables;
    }
}
