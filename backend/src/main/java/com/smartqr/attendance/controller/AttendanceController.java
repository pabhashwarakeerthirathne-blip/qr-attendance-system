package com.smartqr.attendance.controller;

import com.smartqr.attendance.model.AttendanceScanRequest;
import com.smartqr.attendance.model.AttendanceScanResponse;
import com.smartqr.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping("/scan")
    public AttendanceScanResponse scan(@Valid @RequestBody AttendanceScanRequest request) {
        return service.scan(request);
    }
}
