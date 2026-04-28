package com.example.notification;

import com.example.notification.dto.MessageRequest;
import com.example.notification.model.*;
import com.example.notification.repository.MessageRepository;
import com.example.notification.repository.NotificationLogRepository;
import com.example.notification.repository.UserRepository;
import com.example.notification.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationSender smsSender;

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationSender pushSender;

    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        notificationService = new NotificationService(userRepository, messageRepository, logRepository, smsSender, emailSender, pushSender);
    }

    @Test
    public void testSendNotification() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setBody("Test message");

        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPhone("123456789");
        user.setSubscribedCategories(Arrays.asList(Category.SPORTS));
        user.setChannels(Arrays.asList(Channel.EMAIL, Channel.SMS));

        List<User> users = Arrays.asList(user);

        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setCategory(Category.SPORTS);
        savedMessage.setBody("Test message");

        when(userRepository.findAll()).thenReturn(users);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Act
        notificationService.sendNotification(request);

        // Assert
        verify(messageRepository).save(any(Message.class));
        verify(smsSender).send("123456789", "Test message");
        verify(emailSender).send("john@example.com", "Test message");
        verify(pushSender, never()).send(anyString(), anyString());
        verify(logRepository, times(2)).save(any(NotificationLog.class));
    }

    @Test
    public void testSendNotification_UserNotSubscribed() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.FINANCE);
        request.setBody("Finance message");

        User user = new User();
        user.setId(1L);
        user.setSubscribedCategories(Arrays.asList(Category.SPORTS)); // Not subscribed to FINANCE

        List<User> users = Arrays.asList(user);

        Message savedMessage = new Message();
        savedMessage.setId(1L);

        when(userRepository.findAll()).thenReturn(users);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        // Act
        notificationService.sendNotification(request);

        // Assert
        verify(smsSender, never()).send(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString());
        verify(pushSender, never()).send(anyString(), anyString());
        verify(logRepository, never()).save(any(NotificationLog.class));
    }

    @Test
    public void testSendNotification_SendFailure() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setBody("Test message");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setSubscribedCategories(Arrays.asList(Category.SPORTS));
        user.setChannels(Arrays.asList(Channel.EMAIL));

        List<User> users = Arrays.asList(user);

        Message savedMessage = new Message();
        savedMessage.setId(1L);

        when(userRepository.findAll()).thenReturn(users);
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
        doThrow(new RuntimeException("Send failed")).when(emailSender).send(anyString(), anyString());

        // Act
        notificationService.sendNotification(request);

        // Assert
        verify(emailSender).send("john@example.com", "Test message");
        verify(logRepository).save(any(NotificationLog.class));
        // Check that status is FAILED, but since it's saved, verify save is called
    }
}