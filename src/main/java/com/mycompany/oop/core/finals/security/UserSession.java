package com.mycompany.oop.core.finals.security;

import com.mycompany.oop.core.finals.model.User;

public class UserSession {

    private User currentUser;

    public void startSession(User user) {
        if (user == null || !user.isActive()) {
            throw new IllegalArgumentException(
                "Cannot start a session for an invalid user."
            );
        }

        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void endSession() {
        currentUser = null;
    }
}