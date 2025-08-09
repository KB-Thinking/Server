package org.sopt.controller;

import org.sopt.ChatConverter;
import org.sopt.common.response.ApiResponse;
import org.sopt.dto.ChatData;
import org.sopt.dto.ChatRequest;
import org.sopt.dto.ChatResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatConverter orchestrator;

    public ChatController(ChatConverter orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<ChatData>> chat(
            @PathVariable Long userId,
            @RequestBody ChatRequest req
    ) {
        ChatResponse result = orchestrator.handle(userId, req.requestText());
        ChatData data = new ChatData(result.responseText(), result.route());
        return ResponseEntity.ok(ApiResponse.success(200, "응답이 성공적으로 반환되었습니다", data));
    }
}