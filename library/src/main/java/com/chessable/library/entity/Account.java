package com.chessable.library.entity;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "account")
@Entity
public class Account {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private String email;
    private String username;
    private String password;

    @Column(name = "accessed_at", nullable = false)
    private Instant accessedAt;

    @Column(name = "rating_low", nullable = false)
    private int ratingLow;

    @Column(name = "rating_high", nullable = false)
    private int ratingHigh;

    @Column(name = "new_user", nullable = false)
    private boolean newUser;

    public Account() {
        this.newUser = true;
        this.ratingLow = 800;
        this.ratingHigh = 900;
        this.accessedAt = Instant.now();
    }

    public Account(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.newUser = true;
        this.ratingLow = 800;
        this.ratingHigh = 900;
        this.accessedAt = Instant.now();
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
    


    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public int getRatingLow() {
        return ratingLow;
    }

    public void setRatingLow(int ratingLow) {
        this.ratingLow = ratingLow;
    }

    public int getRatingHigh() {
        return ratingHigh;
    }

    public void setRatingHigh(int ratingHigh) {
        this.ratingHigh = ratingHigh;
    }

    public Instant getAccessedAt() {
        return accessedAt;
    }

    public void setAccessedAt(Instant accessedAt) {
        this.accessedAt = accessedAt;
    }
    
}
