package com.smartqr.attendance.model;

import jakarta.validation.constraints.NotBlank;

public record AttendanceScanRequest(
        @NotBlank String studentNumber,
        @NotBlank String sessionId,
        String scannedBy
) {}
