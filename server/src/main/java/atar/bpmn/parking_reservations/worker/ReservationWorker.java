package atar.bpmn.parking_reservations.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class ReservationWorker {

    @JobWorker(type = "check_for_spot")
    public Map<String, Object> checkForSpot(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("check for spot");
        System.out.println(jobResultVariables);
        return jobResultVariables;
    }

    @JobWorker(type = "prepare_price")
    public Map<String, Object> preparePrice(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("prepare_price");

        return jobResultVariables;
    }

    @JobWorker(type = "prepare_reservation")
    public Map<String, Object> prepareReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("prepare_reservation");

        return jobResultVariables;
    }

    @JobWorker(type = "send_ticket")
    public Map<String, Object> sendTicket(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_ticket");

        return jobResultVariables;
    }

    @JobWorker(type = "cancel_reservation")
    public Map<String, Object> cancelReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("cancel_reservation");

        return jobResultVariables;
    }

    @JobWorker(type = "finalize_reservation")
    public Map<String, Object> finaleReservation(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("finalize_reservation");

        return jobResultVariables;
    }

    @JobWorker(type = "send_cancellation_message")
    public Map<String, Object> sendCancellationMessage(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_cancellation_message");

        return jobResultVariables;
    }

    @JobWorker(type = "send_no_space_message")
    public Map<String, Object> sendNoSpaceMessage(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("send_no_space_message");

        return jobResultVariables;
    }

}
