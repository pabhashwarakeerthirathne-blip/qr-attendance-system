package com.smartqr.attendance.model;

import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String studentNumber,
        @NotBlank String fullName,
        String course,
        String batch
) {}
