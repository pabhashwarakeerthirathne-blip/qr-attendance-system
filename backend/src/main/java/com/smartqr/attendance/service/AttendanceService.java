package com.smartqr.attendance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartqr.attendance.config.SupabaseClient;
import com.smartqr.attendance.model.AttendanceScanRequest;
import com.smartqr.attendance.model.AttendanceScanResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class AttendanceService {

    private final SupabaseClient supabase;

    public AttendanceService(SupabaseClient supabase) {
        this.supabase = supabase;
    }

    public AttendanceScanResponse scan(AttendanceScanRequest request) {
        try {
            // 1. Try to find the student by their scanned ID
            JsonNode[] students = supabase.client()
                    .get()
                    .uri(uri -> uri.path("/students")
                            .queryParam("student_number", "eq." + request.studentNumber())
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode[].class)
                    .block();

            JsonNode student;

            // 2. If student is not found, Auto-Register them instantly
            if (students == null || students.length == 0) {
                JsonNode[] newStudent = supabase.client()
                        .post()
                        .uri("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Prefer", "return=representation")
                        .bodyValue(Map.of(
                                "student_number", request.studentNumber(),
                                "full_name", request.studentNumber(), // Uses ID as placeholder name
                                "qr_value", request.studentNumber(),
                                "active", true
                        ))
                        .retrieve()
                        .bodyToMono(JsonNode[].class)
                        .block();

                if (newStudent == null || newStudent.length == 0) {
                    return new AttendanceScanResponse(false, "ERROR",
                            "Failed to auto-register student.", null, null);
                }
                student = newStudent[0];
            } else {
                student = students[0];
            }

            // 3. Check if they are already IN (Open Record)
            JsonNode[] openRecords = supabase.client()
                    .get()
                    .uri(uri -> uri.path("/attendance")
                            .queryParam("student_id", "eq." + student.get("id").asText())
                            .queryParam("session_id", "eq." + request.sessionId())
                            .queryParam("out_time", "is.null")
                            .queryParam("select", "*")
                            .build())
                    .retrieve()
                    .bodyToMono(JsonNode[].class)
                    .block();

            // 4. Mark OUT if an open record exists
            if (openRecords != null && openRecords.length > 0) {
                JsonNode record = openRecords[0];
                String id = record.get("id").asText();

                JsonNode[] updated = supabase.client()
                        .patch()
                        .uri(uri -> uri.path("/attendance")
                                .queryParam("id", "eq." + id)
                                .build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("out_time", OffsetDateTime.now().toString()))
                        .retrieve()
                        .bodyToMono(JsonNode[].class)
                        .block();

                return new AttendanceScanResponse(true, "OUT",
                        "Attendance marked OUT.", student,
                        updated != null && updated.length > 0 ? updated[0] : record);
            }

            // 5. Mark IN if no open record exists
            JsonNode[] created = supabase.client()
                    .post()
                    .uri("/attendance")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Prefer", "return=representation")
                    .bodyValue(Map.of(
                            "student_id", student.get("id").asText(),
                            "session_id", request.sessionId(),
                            "in_time", OffsetDateTime.now().toString(),
                            "scanned_by", request.scannedBy()
                    ))
                    .retrieve()
                    .bodyToMono(JsonNode[].class)
                    .block();

            return new AttendanceScanResponse(true, "IN",
                    "Attendance marked IN.", student,
                    created != null && created.length > 0 ? created[0] : null);

        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Supabase request failed: " + e.getResponseBodyAsString(), e);
        }
    }
}