package com.example.cap.Controller;

import com.example.cap.Service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendEmail() {

        emailService.sendEmail(
                "hcosh.21@gmail.com",
                "Test Email",
                "Hello from Cap!"
        );

        return "Email sent successfully";
    }
}