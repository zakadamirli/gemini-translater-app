package com.zekademirli.controller;

import com.zekademirli.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        String answer = qnaService.getAnswer(question);
        return ResponseEntity.ok(answer);
    }

//    @PostMapping("test")
//    public ResponseEntity<String> getQuestion() {
//        return ResponseEntity.ok(qnaService.test());
//    }
}
