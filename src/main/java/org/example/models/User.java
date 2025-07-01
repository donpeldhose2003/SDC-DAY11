package org.example.models;

public class User {
    private String name;
    private String email;

    public User() {}  // Empty constructor

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and setters
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}
