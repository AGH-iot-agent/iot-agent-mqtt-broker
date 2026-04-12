package io.agh.iot.mqtt.controller;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class MqttController {

    private final Map<String, List<Message>> messages = new ConcurrentHashMap<>();

    @GetMapping("/messages/{topic}")
    public List<Message> getMessages(@PathVariable String topic) {
        return messages.getOrDefault(topic, Collections.emptyList());
    }

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.CREATED)
    public PublishResponse publish(@RequestBody PublishRequest request) {
        Message msg = new Message(Instant.now().toString(), request.getPayload());
        messages.computeIfAbsent(request.getTopic(), k -> new ArrayList<>()).add(msg);
        return new PublishResponse(request.getTopic(), msg);
    }

    @Data
    static class PublishRequest {
        private String topic;
        private Map<String, Object> payload;
    }

    record Message(String timestamp, Map<String, Object> payload) {}
    record PublishResponse(String topic, Message message) {}
}
