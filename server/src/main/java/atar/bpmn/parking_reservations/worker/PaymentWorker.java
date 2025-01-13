package atar.bpmn.parking_reservations.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import atar.bpmn.parking_reservations.model.Card;
import atar.bpmn.parking_reservations.service.PyamentService;

import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor
public class PaymentWorker {

    private static final String BANK_ERROR = "BANK_ERROR";

    private final PyamentService pyamentService;

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

        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM").toFormatter(Locale.ENGLISH);
        try {
            Card cardData = new Card(
                jobResultVariables.get("cardNumber").toString(),
                jobResultVariables.get("cardName").toString(), 
                jobResultVariables.get("cardCvc").toString(), 
                YearMonth.parse(jobResultVariables.get("cardExpire").toString(), formatter));
    
            if(!pyamentService.validateCardData(null)) {
                sendWorkerError(client, job);
            }
            pyamentService.makePayment(cardData, Integer.parseInt(jobResultVariables.get("totalCost").toString()));
        } catch (Exception e) {
            System.out.println(e.toString());
            sendWorkerError(client, job);
        }
 
        return jobResultVariables;
    }

    private void sendWorkerError(final JobClient client, ActivatedJob job) {
        client.newThrowErrorCommand(job.getKey())
        .errorCode("BANK_ERROR")
        .send()
        .join();
    }
}
