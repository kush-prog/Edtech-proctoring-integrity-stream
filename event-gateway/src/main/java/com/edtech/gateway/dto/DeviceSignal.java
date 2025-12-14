package com.edtech.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSignal {
    @NotBlank
    private String signalId;
    @NotBlank
    private String sessionId;
    @NotBlank
    private String signalType; // CAMERA_DISCONNECT, MIC_MUTE, NETWORK_LOSS
    private long timestamp;
    private int severity; // 1-10
}
