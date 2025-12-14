package com.edtech.audit.service;

import com.edtech.audit.dto.SuspicionScore;
import com.edtech.audit.entity.AuditLog;
import com.edtech.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(topics = "${app.topic.suspicion-scores}", groupId = "audit-group")
    public void consumeSuspicionScore(SuspicionScore score) {
        log.info("Received suspicion score: {}", score);
        AuditLog auditLog = AuditLog.builder()
                .sessionId(score.getSessionId())
                .eventType("SUSPICION_SCORE")
                .score(score.getScore())
                .reason(score.getReason())
                .createdAt(LocalDateTime.now())
                .payload(score.toString())
                .build();
        auditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "${app.topic.dlq}", groupId = "audit-dlq-group")
    public void consumeDlq(Object message) {
        log.warn("Received DLQ message: {}", message);
        AuditLog auditLog = AuditLog.builder()
                .eventType("DLQ_EVENT")
                .payload(message.toString())
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(auditLog);
    }
}
