package com.example.notification.service;

public interface NotificationSender {
    void send(String recipient, String message);
}