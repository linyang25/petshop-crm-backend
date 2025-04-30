package com.petshop.crmbackend.dto;

public class LoginResponse {

    private String userId;
    private String username;
    private String role;
    private String token;

    public LoginResponse(String userId, String username, String role, String token) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.token = token;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
