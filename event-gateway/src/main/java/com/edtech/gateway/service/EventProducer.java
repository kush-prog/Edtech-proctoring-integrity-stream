package com.edtech.gateway.service;

import com.edtech.gateway.dto.DeviceSignal;
import com.edtech.gateway.dto.ExamEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.topic.exam-events}")
    private String examEventsTopic;

    @Value("${app.topic.device-signals}")
    private String deviceSignalsTopic;

    @Value("${app.topic.dlq}")
    private String dlqTopic;

    public void sendExamEvent(ExamEvent event) {
        try {
            log.info("Publishing exam event: {}", event);
            kafkaTemplate.send(examEventsTopic, event.getSessionId(), event);
        } catch (Exception e) {
            log.error("Failed to publish exam event", e);
            sendToDlq(event, e.getMessage());
        }
    }

    public void sendDeviceSignal(DeviceSignal signal) {
        try {
            log.info("Publishing device signal: {}", signal);
            kafkaTemplate.send(deviceSignalsTopic, signal.getSessionId(), signal);
        } catch (Exception e) {
            log.error("Failed to publish device signal", e);
            sendToDlq(signal, e.getMessage());
        }
    }

    private void sendToDlq(Object message, String error) {
        log.warn("Sending message to DLQ: {}", message);
        // Wrap with error info if needed, for now sending raw object
        kafkaTemplate.send(dlqTopic, message);
    }
}
