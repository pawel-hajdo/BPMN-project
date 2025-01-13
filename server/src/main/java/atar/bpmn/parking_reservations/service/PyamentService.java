package atar.bpmn.parking_reservations.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import atar.bpmn.parking_reservations.model.Card;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

@Service
public class PyamentService {
    private Map<Card, Integer> cards;

    public PyamentService() {
        cards=new HashMap<>();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM").toFormatter(Locale.ENGLISH);
        cards.put(
            new Card(
                "1231231231231231", 
                "Piotr Dawid",
                "123",
                YearMonth.parse("2025-01", formatter)
            ), 2137
        );
    }

    public boolean validateCardData(Card cardData) {
        return true;
    }

    public Integer makePayment(Card cardData, Integer payment) throws Exception {
        Integer money = cards.get(cardData);

        if(money == null) {
            throw new Exception("Not enough money");
        }
        money -= payment;

        if(money < 0) {
            throw new Exception("Not enough money");
        }

        cards.put(cardData, money);
        return money;
    }
}
