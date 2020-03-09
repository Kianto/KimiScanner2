package com.app.kimiscanner.account;

public class UserAccount {

    private String id;
    private String email;

    public UserAccount(String userID, String email) {
        this.id = userID;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

}
