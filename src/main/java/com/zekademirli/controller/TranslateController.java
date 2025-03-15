package com.zekademirli.controller;

import com.zekademirli.service.TranslateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class TranslateController {

    private final TranslateService translateService;

    public TranslateController(TranslateService translateService) {
        this.translateService = translateService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/gemini/word-info")
    public ResponseEntity<String> askQuestion(@RequestParam Map<String, String> payload) {
        String question = payload.get("question");
        String answer = translateService.getAnswer(question);
        return ResponseEntity.ok(answer);
    }
}
