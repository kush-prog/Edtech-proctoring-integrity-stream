package com.edtech.audit.controller;

import com.edtech.audit.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping
    public ResponseEntity<String> replaySession(@RequestParam String sessionId,
            @RequestParam(defaultValue = "exam-archives") String bucket) {
        replayService.replaySession(sessionId, bucket);
        return ResponseEntity.ok("Replay initiated for session: " + sessionId);
    }
}
