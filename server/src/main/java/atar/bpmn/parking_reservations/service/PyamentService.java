package atar.bpmn.parking_reservations.service;

import java.util.Map;

import atar.bpmn.parking_reservations.model.Card;

public class PyamentService {
    private Map<Card, Integer> cards;

    public boolean validateCardData(Card cardData) {
        return false;
    }

    public Integer makePayment(Card cardData, Integer payment) throws Exception {
        Integer money = cards.get(cardData);
        money -= payment;

        if(money < 0) {
            throw new Exception("Not enough money");
        }

        cards.put(cardData, money);
        return money;
    }
}
