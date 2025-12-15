package com.gautam.notificationservice.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @PostMapping("/send")
    public String sendNotification(@RequestParam String message) {
        // Here you can integrate email/SMS logic
        return "Notification sent: " + message;
    }
}
