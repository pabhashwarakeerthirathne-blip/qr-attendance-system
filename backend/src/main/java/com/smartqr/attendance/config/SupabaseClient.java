package com.smartqr.attendance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SupabaseClient {
    private final WebClient client;

    public SupabaseClient(
            @Value("${supabase.url}") String url,
            @Value("${supabase.key}") String key) {
        this.client = WebClient.builder()
                .baseUrl(url + "/rest/v1")
                .defaultHeader("apikey", key)
                .defaultHeader("Authorization", "Bearer " + key)
                .build();
    }

    public WebClient client() {
        return client;
    }
}
