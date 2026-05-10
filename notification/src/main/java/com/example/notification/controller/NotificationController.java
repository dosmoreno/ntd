package com.example.notification.controller;

import com.example.notification.dto.MessageRequest;
import com.example.notification.model.Category;
import com.example.notification.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("messageRequest", new MessageRequest());
        model.addAttribute("categories", Category.values());
        model.addAttribute("logs", notificationService.getLogs());
        return "index";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute MessageRequest messageRequest, Model model) {
        if (messageRequest.getBody() == null || messageRequest.getBody().trim().isEmpty()) {
            model.addAttribute("error", "Message body cannot be empty");
            model.addAttribute("messageRequest", messageRequest);
            model.addAttribute("categories", Category.values());
            model.addAttribute("logs", notificationService.getLogs());
            return "index";
        }
        notificationService.sendNotification(messageRequest);
        model.addAttribute("messageRequest", new MessageRequest());
        model.addAttribute("categories", Category.values());
        model.addAttribute("logs", notificationService.getLogs());
        model.addAttribute("success", "Message sent successfully");
        return "index";
    }
}