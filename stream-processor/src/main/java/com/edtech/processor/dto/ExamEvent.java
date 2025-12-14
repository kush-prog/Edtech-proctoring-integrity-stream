package com.edtech.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamEvent {
    private String eventId;
    private String sessionId;
    private String examId;
    private String studentId;
    private String eventType;
    private long timestamp;
    private String metadata;
}
