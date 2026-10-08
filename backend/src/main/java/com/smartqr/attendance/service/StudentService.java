package com.smartqr.attendance.service;

import com.smartqr.attendance.config.SupabaseClient;
import com.smartqr.attendance.model.StudentRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class StudentService {
    private final SupabaseClient supabase;

    public StudentService(SupabaseClient supabase) {
        this.supabase = supabase;
    }

    public Object list() {
        return supabase.client().get()
                .uri("/students?select=*&order=student_number.asc")
                .retrieve().bodyToMono(Object.class).block();
    }

    public Object create(StudentRequest request) {
        return supabase.client().post()
                .uri("/students")
                .header("Prefer", "return=representation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "student_number", request.studentNumber(),
                        "full_name", request.fullName(),
                        "course", request.course() == null ? "" : request.course(),
                        "batch", request.batch() == null ? "" : request.batch(),
                        "qr_value", request.studentNumber(),
                        "active", true
                ))
                .retrieve().bodyToMono(Object.class).block();
    }
}
