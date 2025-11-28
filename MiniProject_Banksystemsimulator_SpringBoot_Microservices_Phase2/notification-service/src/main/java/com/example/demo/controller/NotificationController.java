package com.example.demo.controller;

import com.example.demo.dto.NotificationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationDto notification) {
        System.out.println("----------------------------------------");
        System.out.println("📣 New Notification for: " + notification.getRecipient());
        System.out.println("   Message: " + notification.getMessage());
        System.out.println("----------------------------------------");
        
        // In a real application, this would call an email/SMS provider
        return ResponseEntity.ok("Notification logged successfully.");
    }
}