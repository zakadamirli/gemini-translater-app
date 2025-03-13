package com.zekademirli.controller;

import com.zekademirli.service.QnaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qna")
public class AIController {

    private final QnaService qnaService;

    public AIController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestParam Map<String, String> payload) {
        String question = payload.get("question");
        String answer = qnaService.getAnswer(question);
        return ResponseEntity.ok(answer);
    }

//    @PostMapping("test")
//    public ResponseEntity<String> getQuestion() {
//        return ResponseEntity.ok(qnaService.test());
//    }
}
