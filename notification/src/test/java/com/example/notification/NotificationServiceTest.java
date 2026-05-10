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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.ArgumentCaptor;

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
        List<NotificationSender> senders = Arrays.asList(smsSender, emailSender, pushSender);
        when(smsSender.getChannel()).thenReturn(Channel.SMS);
        when(emailSender.getChannel()).thenReturn(Channel.EMAIL);
        when(pushSender.getChannel()).thenReturn(Channel.PUSH);
        notificationService = new NotificationService(userRepository, messageRepository, logRepository, senders);
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

        // Verify the log status is "FAILED"
        ArgumentCaptor<NotificationLog> logCaptor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(logCaptor.capture());
        NotificationLog capturedLog = logCaptor.getValue();
        assertEquals("FAILED", capturedLog.getStatus());
    }

    @Test
    public void testSendNotification_EmptyUserList() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setBody("Test message");

        List<User> users = Collections.emptyList();

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
    public void testSendNotification_UserSubscribedButNoChannels() {
        // Arrange
        MessageRequest request = new MessageRequest();
        request.setCategory(Category.SPORTS);
        request.setBody("Test message");

        User user = new User();
        user.setId(1L);
        user.setSubscribedCategories(Arrays.asList(Category.SPORTS));
        user.setChannels(Collections.emptyList()); // No channels

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
    public void testGetRecipient() {
        // Arrange
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        user.setPhone("123456789");

        // Act & Assert
        assertEquals("123456789", notificationService.getRecipient(user, Channel.SMS));
        assertEquals("john@example.com", notificationService.getRecipient(user, Channel.EMAIL));
        assertEquals("John", notificationService.getRecipient(user, Channel.PUSH));
    }
}