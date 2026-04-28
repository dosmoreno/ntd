package com.example.notification.controller;

import com.example.notification.dto.MessageRequest;
import com.example.notification.model.Category;
import com.example.notification.model.NotificationLog;
import com.example.notification.repository.NotificationLogRepository;
import com.example.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationLogRepository logRepository;

    @Autowired
    public NotificationController(NotificationService notificationService, NotificationLogRepository logRepository) {
        this.notificationService = notificationService;
        this.logRepository = logRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("messageRequest", new MessageRequest());
        model.addAttribute("categories", Category.values());
        List<NotificationLog> logs = logRepository.findAll();
        logs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        model.addAttribute("logs", logs);
        return "index";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute MessageRequest messageRequest, Model model) {
        if (messageRequest.getBody() == null || messageRequest.getBody().trim().isEmpty()) {
            model.addAttribute("error", "Message body cannot be empty");
            model.addAttribute("messageRequest", messageRequest);
            model.addAttribute("categories", Category.values());
            List<NotificationLog> logs = logRepository.findAll();
            logs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            model.addAttribute("logs", logs);
            return "index";
        }
        notificationService.sendNotification(messageRequest);
        return "redirect:/";
    }
}