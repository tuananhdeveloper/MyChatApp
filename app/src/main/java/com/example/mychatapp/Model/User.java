package com.example.mychatapp.Model;

public class User {
    private String name;
    private String email;
    private Boolean isOnline;
    private String id;

    public User(){

    }

    public String getId() {
        return id;
    }

    public User(String id, String name, String email, Boolean isOnline) {
        this.name = name;
        this.email = email;
        this.isOnline = isOnline;
        this.id = id;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
