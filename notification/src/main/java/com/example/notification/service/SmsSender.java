package com.example.notification.service;

import com.example.notification.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsSender implements NotificationSender {
    private static final Logger logger = LoggerFactory.getLogger(SmsSender.class);

    @Override
    public Channel getChannel() {
        return Channel.SMS;
    }

    @Override
    public void send(String recipient, String message) {
        logger.info("Sending SMS to {}: {}", recipient, message);
    }
}