package com.bookstore.controller;

import com.bookstore.dto.request.AiChatRequest;
import com.bookstore.dto.response.AiChatResponse;
import com.bookstore.service.AiAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiAgentController {

    private final AiAgentService aiAgentService;

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @RequestBody AiChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails != null && request.getUserEmail() == null) {
            request.setUserEmail(userDetails.getUsername());
        }

        return ResponseEntity.ok(aiAgentService.chat(request));
    }
}