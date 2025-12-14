package com.edtech.audit.dto;

import lombok.Data;

@Data
public class SuspicionScore {
    private String sessionId;
    private double score;
    private String reason;
    private long timestamp;
}
