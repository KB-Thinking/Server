package org.sopt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class GptController {

    private final GptService gpt;

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> req) {
        String prompt = req.getOrDefault("prompt", "");
        if (prompt.isBlank()) return ResponseEntity.badRequest().body("prompt 가 필요합니다.");
        return ResponseEntity.ok(gpt.chat(prompt));
    }
}