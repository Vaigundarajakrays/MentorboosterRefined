package com.mentorboosters.app.controller;

import com.mentorboosters.app.dto.SubscribeDTO;
import com.mentorboosters.app.dto.SubscribeResponseDTO;
import com.mentorboosters.app.exceptionHandling.ResourceNotFoundException;
import com.mentorboosters.app.exceptionHandling.UnexpectedServerException;
import com.mentorboosters.app.response.CommonResponse;
import com.mentorboosters.app.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping
    public CommonResponse<SubscribeResponseDTO> subscribe(@RequestBody SubscribeDTO subscribeDTO) throws UnexpectedServerException, ResourceNotFoundException {
        return subscribeService.subscribe(subscribeDTO.getEmail());
    }

}
