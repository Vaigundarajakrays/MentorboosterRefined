package com.mentorboosters.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Mono;


import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class AiMentorService {

    private final WebClient webClient;
    private final WebClient moderationClient;

    public AiMentorService(@Value("${openai.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        this.moderationClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/moderations")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    // For this no credits needed, it is free
    public Mono<Boolean> isFlaggedByModeration(String input){

        Map<String, Object> body = Map.of("input", input);

        return moderationClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.path("results").get(0).path("flagged").asBoolean(false))
                .onErrorReturn(false); // fallback to false if API call fails

    }

    public Flux<String> streamResponse(String userMessage) {
        Map<String, Object> request = Map.of(
                "model", "gpt-4o-mini",
                "stream", true,
                "max_tokens", 300,
                "temperature", 1.0,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                                    You are a helpful AI mentor. Keep your responses professional and concise. Never exceed 300 tokens in your replies. Use short, clear sentences. If asked about your identity, always respond: "I am MentorBooster's AI model — your personal learning companion."
                                                   """
                        ),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String rawChunk = new String(bytes, StandardCharsets.UTF_8);
                    return Flux.fromArray(rawChunk.split("\n"));
                })
                .filter(line -> line.startsWith("data: ") && !line.contains("[DONE]"))
                .flatMap(line -> {
                    try {
                        String json = line.substring("data: ".length());
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(json);
                        JsonNode contentNode = root.path("choices").get(0).path("delta").path("content");
                        if (!contentNode.isMissingNode()) {
                            return Flux.just(" " +contentNode.asText());
                        }
                    } catch (Exception e) {
                        return Flux.just(" [ERROR] " + e.getMessage());
                    }
                    return Flux.empty();
                })
                .concatWith(Flux.just(" [DONE]")); // the space before done is very important, that is how frontend is expecting

    }

    // It was correct
//    public Flux<String> streamResponse(String userMessage) {
//        Map<String, Object> request = Map.of(
//                "model", "gpt-4o-mini",
//                "stream", true,
//                "max_tokens", 200,
//                "temperature", 1.0,
//                "messages", List.of(
//                        Map.of("role", "system", "content", "You are a helpful AI mentor. Keep responses professional and short. If asked about your identity, always respond:\"I am MentorBooster's AI model — your personal learning companion.\""),
//                        Map.of("role", "user", "content", userMessage)
//                )
//        );
//
//        return webClient.post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .accept(MediaType.TEXT_EVENT_STREAM)
//                .retrieve()
//                .bodyToFlux(DataBuffer.class)
//                .flatMap(dataBuffer -> {
//                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                    dataBuffer.read(bytes);
//                    DataBufferUtils.release(dataBuffer);
//                    String rawChunk = new String(bytes, StandardCharsets.UTF_8);
//                    // Split by newlines because SSE sends data line-by-line
//                    String[] lines = rawChunk.split("\n");
//                    for (String line : lines) {
//                        if (line.startsWith("data: ")) {
//                            System.out.println("👉 RAW LINE FROM OPENAI: " + line);
//                        }
//                    }
//                    return Flux.fromArray(rawChunk.split("\n"));
//                })
//                .filter(line -> line.startsWith("data: ") && !line.contains("[DONE]"))
//                .flatMap(line -> {
//                    try {
//                        String json = line.substring("data: ".length());
//                        ObjectMapper mapper = new ObjectMapper();
//                        JsonNode root = mapper.readTree(json);
//                        JsonNode contentNode = root.path("choices").get(0).path("delta").path("content");
//                        if (!contentNode.isMissingNode()) {
//                            return Flux.just(contentNode.asText());
//                        }
//                    } catch (Exception e) {
//                        return Flux.empty();
//                    }
//                    return Flux.empty();
//                });
//    }


//    public Flux<String> streamResponse(String userMessage) {
//
//        Map<String, Object> request = Map.of(
//                "model", "gpt-4o-mini",
//                "stream", true,
//                "max_tokens", 150,
//                "temperature", 0.7,
//                "messages", List.of(
//                        Map.of("role", "system", "content", "You are a helpful AI mentor. Keep responses professional and short. If asked about your identity, always respond:\"I am MentorBooster's AI model — your personal learning companion.\""),
//                        Map.of("role", "user", "content", userMessage)
//                )
//        );
//
//        return webClient.post()
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .accept(MediaType.TEXT_EVENT_STREAM) // “Please send the response using text/event-stream format (aka SSE chunks)
//                .retrieve()
//                .bodyToFlux(DataBuffer.class)
//                .map(dataBuffer -> {
//                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                    dataBuffer.read(bytes);
//                    DataBufferUtils.release(dataBuffer);
//                    return new String(bytes, StandardCharsets.UTF_8);
//                })
//                .flatMap(line -> {
//                    // Filter lines that start with "data: " and contain content
//                    if (line.startsWith("data: ") && !line.contains("[DONE]")) {
//                        try {
//                            String json = line.substring("data: ".length());
//                            ObjectMapper mapper = new ObjectMapper();
//                            JsonNode root = mapper.readTree(json);
//                            JsonNode contentNode = root.path("choices").get(0).path("delta").path("content");
//                            if (!contentNode.isMissingNode()) {
//                                return Flux.just(contentNode.asText());
//                            }
//                        } catch (Exception e) {
//                            return Flux.empty();
//                        }
//                    }
//                    return Flux.empty();
//                });
//    }
}
