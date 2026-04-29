package com.example.notification.service;

import com.example.notification.dto.MessageRequest;
import com.example.notification.model.*;
import com.example.notification.repository.MessageRepository;
import com.example.notification.repository.NotificationLogRepository;
import com.example.notification.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final NotificationLogRepository logRepository;
    private final Map<Channel, NotificationSender> senders;

    public NotificationService(UserRepository userRepository, MessageRepository messageRepository, NotificationLogRepository logRepository, NotificationSender smsSender, NotificationSender emailSender, NotificationSender pushSender) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.logRepository = logRepository;
        this.senders = Map.of(
            Channel.SMS, smsSender,
            Channel.EMAIL, emailSender,
            Channel.PUSH, pushSender
        );
    }

    public void sendNotification(MessageRequest request) {
        Message message = new Message();
        message.setCategory(request.getCategory());
        message.setBody(request.getBody());
        message = messageRepository.save(message);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getSubscribedCategories().contains(request.getCategory())) {
                for (Channel channel : user.getChannels()) {
                    NotificationSender sender = senders.get(channel);
                    String recipient = getRecipient(user, channel);
                    try {
                        sender.send(recipient, request.getBody());
                        logNotification(message.getId(), user.getId(), channel, "SENT");
                    } catch (Exception e) {
                        logNotification(message.getId(), user.getId(), channel, "FAILED");
                    }
                }
            }
        }
    }

    private String getRecipient(User user, Channel channel) {
        return switch (channel) {
            case SMS -> user.getPhone();
            case EMAIL -> user.getEmail();
            case PUSH -> user.getName(); // or some id
        };
    }

    private void logNotification(Long messageId, Long userId, Channel channel, String status) {
        NotificationLog log = new NotificationLog();
        log.setMessageId(messageId);
        log.setUserId(userId);
        log.setChannel(channel);
        log.setTimestamp(LocalDateTime.now());
        log.setStatus(status);
        logRepository.save(log);
    }
}
