package com.pack.pack.application.topic.activity.model;

/**
 * Created by Saurav on 10-04-2016.
 */
public class UserInfo {

    private String username;

    private String password;

    public UserInfo(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
