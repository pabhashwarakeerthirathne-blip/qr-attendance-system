package com.smartqr.attendance.model;

import jakarta.validation.constraints.NotBlank;

public record SessionRequest(
        @NotBlank String sessionName,
        @NotBlank String attendanceDate,
        String startTime,
        String endTime
) {}
