package atar.bpmn.parking_reservations.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.json.JSONObject;

public interface EmitterService {
    SseEmitter addListener(String listenerId);
    void sendMessageToListener(String listenerId, JSONObject message);
    void removeListener(String listenerId);
}
