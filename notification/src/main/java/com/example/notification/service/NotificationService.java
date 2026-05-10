package com.example.notification.service;

import com.example.notification.dto.MessageRequest;
import com.example.notification.model.*;
import com.example.notification.repository.MessageRepository;
import com.example.notification.repository.NotificationLogRepository;
import com.example.notification.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final NotificationLogRepository logRepository;
    private final Map<Channel, NotificationSender> senders;

    public NotificationService(UserRepository userRepository, MessageRepository messageRepository, NotificationLogRepository logRepository, List<NotificationSender> senderBeans) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.logRepository = logRepository;
        this.senders = senderBeans.stream()
            .collect(Collectors.toMap(NotificationSender::getChannel, sender -> sender));
    }

    public List<NotificationLog> getLogs() {
        List<NotificationLog> logs = logRepository.findAll();
        logs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return logs;
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
                        logNotification(message, user, channel, recipient, "SENT");
                    } catch (Exception e) {
                        logger.error("Failed to send {} notification to user {}: {}", channel, user.getId(), e.getMessage(), e);
                        logNotification(message, user, channel, recipient, "FAILED");
                    }
                }
            }
        }
    }

    public String getRecipient(User user, Channel channel) {
        return switch (channel) {
            case SMS -> user.getPhone();
            case EMAIL -> user.getEmail();
            case PUSH -> user.getName(); // or some id
        };
    }

    private void logNotification(Message message, User user, Channel channel, String recipient, String status) {
        NotificationLog log = new NotificationLog();
        log.setMessage(message);
        log.setUserId(user.getId());
        log.setUserName(user.getName());
        log.setRecipient(recipient);
        log.setCategory(message.getCategory());
        log.setChannel(channel);
        log.setTimestamp(LocalDateTime.now());
        log.setStatus(status);
        logRepository.save(log);
    }
}