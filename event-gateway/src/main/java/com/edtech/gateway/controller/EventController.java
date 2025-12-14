package com.edtech.gateway.controller;

import com.edtech.gateway.dto.DeviceSignal;
import com.edtech.gateway.dto.ExamEvent;
import com.edtech.gateway.service.EventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventProducer eventProducer;

    @PostMapping("/events")
    public ResponseEntity<String> publishEvent(@Valid @RequestBody ExamEvent event) {
        eventProducer.sendExamEvent(event);
        return ResponseEntity.ok("Event received");
    }

    @PostMapping("/signals")
    public ResponseEntity<String> publishSignal(@Valid @RequestBody DeviceSignal signal) {
        eventProducer.sendDeviceSignal(signal);
        return ResponseEntity.ok("Signal received");
    }
}
