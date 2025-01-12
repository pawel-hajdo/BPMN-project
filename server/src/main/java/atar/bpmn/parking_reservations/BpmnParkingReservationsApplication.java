package atar.bpmn.parking_reservations;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class BpmnParkingReservationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BpmnParkingReservationsApplication.class, args);
	}

}
