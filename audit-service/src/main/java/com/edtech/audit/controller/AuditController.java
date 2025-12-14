package com.edtech.audit.controller;

import com.edtech.audit.entity.AuditLog;
import com.edtech.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public List<AuditLog> getAllAudits() {
        return auditLogRepository.findAll();
    }

    @GetMapping("/session/{sessionId}")
    public List<AuditLog> getAuditsBySession(@PathVariable String sessionId) {
        return auditLogRepository.findBySessionId(sessionId);
    }
}
