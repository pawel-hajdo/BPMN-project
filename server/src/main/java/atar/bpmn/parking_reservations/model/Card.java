package atar.bpmn.parking_reservations.model;

import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Card {
    private String number;
    private String name;
    private String cvc; 
    private YearMonth expire;
}
