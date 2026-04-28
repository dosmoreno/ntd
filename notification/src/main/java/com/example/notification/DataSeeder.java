package com.example.notification;

import com.example.notification.model.Category;
import com.example.notification.model.Channel;
import com.example.notification.model.User;
import com.example.notification.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setName("John Doe");
            user1.setEmail("john@example.com");
            user1.setPhone("123456789");
            user1.setSubscribedCategories(Arrays.asList(Category.SPORTS, Category.FINANCE));
            user1.setChannels(Arrays.asList(Channel.EMAIL, Channel.SMS));
            userRepository.save(user1);

            User user2 = new User();
            user2.setName("Jane Smith");
            user2.setEmail("jane@example.com");
            user2.setPhone("987654321");
            user2.setSubscribedCategories(Arrays.asList(Category.MOVIES));
            user2.setChannels(Arrays.asList(Channel.PUSH));
            userRepository.save(user2);

            // Add more users as needed
        }
    }
}