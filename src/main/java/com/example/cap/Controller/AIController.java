package com.example.cap.Controller;

import com.example.cap.Service.AIService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@AllArgsConstructor
public class AIController {

    private final AIService aiService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> analyzeProject(@PathVariable Integer projectId) {
        String result = aiService.analyzeProject(projectId);
        return ResponseEntity.status(200).body(result);
    }
}