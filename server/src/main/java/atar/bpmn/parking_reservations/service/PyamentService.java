package atar.bpmn.parking_reservations.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;

import atar.bpmn.parking_reservations.model.Card;

@Service
public class PyamentService {
    private Map<String, Integer> cards;

    public PyamentService() {
        cards = new HashMap<>();

        // DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM").toFormatter(Locale.ENGLISH);
        cards.put("1231231231231231", 21370
        );
    }

    public boolean validateCardData(Card cardData) {
        return true;
    }

    public Integer makePayment(Card cardData, Integer payment) throws Exception {
        Integer money = cards.get(cardData.getNumber());

        System.out.println(money + " before");
        if(money == null) {
            throw new Exception("Not enough money");
        }
        money -= payment;
        
        System.out.println(money + " after");
        if(money < 0) {
            throw new Exception("Not enough money");
        }

        cards.put(cardData.getNumber(), money);
        return money;
    }
}
