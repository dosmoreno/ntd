package com.example.notification.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Category> subscribedCategories;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<Channel> channels;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public List<Category> getSubscribedCategories() { return subscribedCategories; }
    public void setSubscribedCategories(List<Category> subscribedCategories) { this.subscribedCategories = subscribedCategories; }
    public List<Channel> getChannels() { return channels; }
    public void setChannels(List<Channel> channels) { this.channels = channels; }
}