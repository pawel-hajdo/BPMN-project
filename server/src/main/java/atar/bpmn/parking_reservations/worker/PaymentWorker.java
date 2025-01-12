package atar.bpmn.parking_reservations.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class PaymentWorker {

    @JobWorker(type = "check_payment_data")
    public Map<String, Object> checkPaymentData(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("check_payment_data");

        return jobResultVariables;
    }

    @JobWorker(type = "save_payment")
    public Map<String, Object> savePayment(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("save_payment");

        return jobResultVariables;
    }

    @JobWorker(type = "complete_payment")
    public Map<String, Object> completePayment(final JobClient client, final ActivatedJob job) {
        var jobResultVariables = job.getVariablesAsMap();

        System.out.println("complete_payment");

        return jobResultVariables;
    }
}
