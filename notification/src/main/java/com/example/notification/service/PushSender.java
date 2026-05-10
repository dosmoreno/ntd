package com.example.notification.service;

import com.example.notification.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(PushSender.class);

    @Override
    public Channel getChannel() {
        return Channel.PUSH;
    }

    @Override
    public void send(String recipient, String message) {
        logger.info("Sending Push to {}: {}", recipient, message);
    }
}