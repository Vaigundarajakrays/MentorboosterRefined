package com.mentorboosters.app.controller;

import com.mentorboosters.app.security.JwtService;
import com.mentorboosters.app.service.AiMentorService;
import com.mentorboosters.app.service.ProfanityCheckerService;
import com.mentorboosters.app.service.TempTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/api/ai-mentor")
@RequiredArgsConstructor
public class AiMentorController {

    private final AiMentorService aiMentorService;
    private final ProfanityCheckerService profanityCheckerService;
    private final TempTokenService tempTokenService;

    // See sse-auth.md in docs package/directory for why this api is used
    @GetMapping("/sse-token")
    public ResponseEntity<String> getSseToken() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String tempToken = tempTokenService.generateToken(username);
        return ResponseEntity.ok(tempToken);
    }


    // The spaces before each message is important
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamAi(@RequestParam String message, @RequestParam String token) {

        if (!tempTokenService.isValid(token)) {
            return Flux.just(" Unauthorized. Invalid or expired token.");
        }

        if (message == null || message.length() > 500) {
            return Flux.just(" Message too long or invalid.");
        }

        if(profanityCheckerService.containsProfanity(message)){
            return Flux.just(" Your message violates our content policy. Please rephrase.");
        }

        return aiMentorService.isFlaggedByModeration(message)
                .flatMapMany(isFlagged -> {
                    if (isFlagged) {
                        return Flux.just(" Your message violates our content policy. Please rephrase.");
                    } else {
                        return aiMentorService.streamResponse(message);
                    }
                });
    }
}

