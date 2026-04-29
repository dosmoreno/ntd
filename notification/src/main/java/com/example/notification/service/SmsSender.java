package com.example.notification.service;

import org.springframework.stereotype.Service;

@Service
public class SmsSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        // Simulate sending SMS
        System.out.println("Sending SMS to " + recipient + ": " + message);
    }
}