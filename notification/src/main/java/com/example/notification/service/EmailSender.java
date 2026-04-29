package com.example.notification.service;

import org.springframework.stereotype.Service;

@Service
public class EmailSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending Email to " + recipient + ": " + message);
    }
}