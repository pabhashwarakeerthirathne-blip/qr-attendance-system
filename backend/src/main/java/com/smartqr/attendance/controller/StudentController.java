package com.smartqr.attendance.controller;

import com.smartqr.attendance.model.StudentRequest;
import com.smartqr.attendance.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public Object list() {
        return service.list();
    }

    @PostMapping
    public Object create(@Valid @RequestBody StudentRequest request) {
        return service.create(request);
    }
}
