package com.app.kimiscanner.account;

public class UserAccount {
    private String username;
    private String password;

    public UserAccount(String username, String password) {
        this.username = username.trim();
        this.password = password.trim();
    }

    public boolean isValid() {
        if (0 == username.length() || 0 == password.length()) {
            return false;
        }

        // todo: check
        return true;
    }

}
