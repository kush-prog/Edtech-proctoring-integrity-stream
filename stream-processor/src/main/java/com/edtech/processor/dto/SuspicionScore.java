package com.edtech.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuspicionScore {
    private String sessionId;
    private double score; // 0-100
    private String reason;
    private long timestamp;
}
