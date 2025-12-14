package com.edtech.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamEvent {
    @NotBlank
    private String eventId;
    @NotBlank
    private String sessionId;
    @NotBlank
    private String examId;
    @NotBlank
    private String studentId;
    @NotBlank
    private String eventType; // TAB_SWITCH, FOCUS_LOST, COPY_PASTE, etc.
    private long timestamp;
    private String metadata; // JSON string for extra details
}
