package com.smartqr.attendance.model;

public record AttendanceScanResponse(
        boolean success,
        String action,
        String message,
        Object student,
        Object attendance
) {}
