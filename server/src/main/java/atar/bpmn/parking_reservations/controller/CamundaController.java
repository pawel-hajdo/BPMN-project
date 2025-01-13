package atar.bpmn.parking_reservations.controller;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import atar.bpmn.parking_reservations.DTO.PaymentBodyTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CamundaController {
    private static final String BPMN_PROCESS_ID = "reservation_process";
    private static final String PAYMENT_MESSAGE_ID = "payment_message";

    @Value("${zeebe.client.cloud.clusterId}")
    private String clusterId;

    @Value("${zeebe.client.cloud.clientId}")
    private String clientId;

    @Value("${zeebe.client.cloud.clientSecret}")
    private String clientSecret;

    @Value("${zeebe.client.cloud.region}")
    private String region;

    private ZeebeClient client;

    @PostConstruct
    public void init() {
        this.client = ZeebeClient.newCloudClientBuilder()
                .withClusterId(clusterId)
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withRegion(region)
                .build();
    }

    @PostMapping("/start")
    public Map<String, Object> startProcessInstance(@RequestBody Map<String, Object> variables) {

        var event = client
                .newCreateInstanceCommand()
                .bpmnProcessId(BPMN_PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send();

        variables.put("processInstanceKey", event.join().getProcessInstanceKey());
        return variables;
    }

    @PostMapping("/payment")
    public String postMethodName(@RequestBody PaymentBodyTemplate paymentData) {

        Map<String, Object> vars = new HashMap<>();

        vars.put("cardNumber", paymentData.card().getNumber());
        vars.put("cardCvc", paymentData.card().getCvc());
        vars.put("cardName", paymentData.card().getName());
        vars.put("cardExpire", paymentData.card().getExpire().toString());
        vars.put("reservationId", paymentData.reservationId());
        vars.put("mail", paymentData.mail());

        client
            .newPublishMessageCommand()
            .messageName(PAYMENT_MESSAGE_ID)
            .correlationKey(paymentData.reservationId().toString())
            .timeToLive(Duration.ofMinutes(30))
            .variables(vars)
            .send()
            .join();
        
        return "{\"message\": \"Processing payment\"}";
    }
}
