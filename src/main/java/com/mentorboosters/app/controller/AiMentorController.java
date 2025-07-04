package com.mentorboosters.app.controller;

import com.mentorboosters.app.service.AiMentorService;
import com.mentorboosters.app.service.ProfanityCheckerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/api/ai-mentor")
@RequiredArgsConstructor
public class AiMentorController {

    private final AiMentorService aiMentorService;
    private final ProfanityCheckerService profanityCheckerService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamAi(@RequestParam String message) {

        if (message == null || message.length() > 500) {
            return Flux.just("Message too long or invalid.");
        }

        if(profanityCheckerService.containsProfanity(message)){
            return Flux.just("Your message violates our content policy. Please rephrase.");
        }

        return aiMentorService.isFlaggedByModeration(message)
                .flatMapMany(isFlagged -> {
                    if (isFlagged) {
                        return Flux.just("Your message violates our content policy. Please rephrase.");
                    } else {
                        return aiMentorService.streamResponse(message);
                    }
                });
    }
}

