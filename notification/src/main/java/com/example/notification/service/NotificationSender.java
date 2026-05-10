package com.example.notification.service;

import com.example.notification.model.Channel;

public interface NotificationSender {
    Channel getChannel();
    void send(String recipient, String message);
}