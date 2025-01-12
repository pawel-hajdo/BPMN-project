package atar.bpmn.parking_reservations.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterService {
    SseEmitter addListener(String listenerId);
    void sendMessageToListener(String listenerId, String listener);
    void removeListener(String listenerId);
}
