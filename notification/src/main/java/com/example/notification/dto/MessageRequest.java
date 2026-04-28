package com.example.notification.dto;

import com.example.notification.model.Category;

public class MessageRequest {
    private Category category;
    private String body;

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}