package com.example.chatapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    // For backward compatibility with WebSocket messages
    public String getNickName() {
        return username;
    }
    
    public void setNickName(String nickName) {
        this.username = nickName;
    }
}
