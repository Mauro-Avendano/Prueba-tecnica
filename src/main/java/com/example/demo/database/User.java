package com.example.demo.database;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    @Column(unique = true)
    private String email;
    private Boolean isActive = true;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PhoneNumber> phoneNumbers;
    private String password;
    private String token;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modified;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime last_login;

    @PrePersist
    private void onCreate() {
        created = LocalDateTime.now();
        modified = LocalDateTime.now();
        last_login = LocalDateTime.now();
    }

    public void setPassword(String password) {
        String hashedPassword = hashPassword(password);
        this.password = hashedPassword;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
