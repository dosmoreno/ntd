package com.example.notification.service;

import org.springframework.stereotype.Service;

@Service
public class PushSender implements NotificationSender {
    @Override
    public void send(String recipient, String message) {
        System.out.println("Sending Push to " + recipient + ": " + message);
    }
}