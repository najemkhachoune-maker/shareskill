package com.example.chatapp.service;

import com.example.chatapp.model.Status;
import com.example.chatapp.model.User;
import com.example.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User saveUser(User user) {
        if (user.getStatus() == null) {
            user.setStatus(Status.ONLINE);
        }
        return repository.save(user);
    }

    public void disconnect(User user) {
        // Try to find by username first (new way)
        Optional<User> storedUser = findByUsername(user.getUsername());
        if (storedUser.isEmpty() && user.getNickName() != null) {
            // Fallback to nickname for backward compatibility
            storedUser = findByUsername(user.getNickName());
        }
        
        if (storedUser.isPresent()) {
            User userToUpdate = storedUser.get();
            userToUpdate.setStatus(Status.OFFLINE);
            repository.save(userToUpdate);
        }
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
    
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
    
    public List<User> findAllUsers() {
        return repository.findAll();
    }
}

