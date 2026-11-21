package com.mycompany.oop.core.finals.security;

import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;

public class AuthorizationService {

    private final UserSession session;

    public AuthorizationService(UserSession session) {
        this.session = session;
    }

    public void requireLogin() {
        if (!session.isLoggedIn()) {
            throw new SecurityException(
                "You must log in first."
            );
        }
    }

    public void requireProductManagementAccess() {
        requireLogin();

        User user = session.getCurrentUser();

        UserRole role = user.getRole();

        if (role != UserRole.CHAIRPERSON
                && role != UserRole.BUSINESS_MANAGER) {
            throw new SecurityException(
                "You do not have permission to manage products."
            );
        }
    }

    public void requireSalesAccess() {
        requireLogin();

        UserRole role = session.getCurrentUser().getRole();

        if (role != UserRole.CHAIRPERSON
                && role != UserRole.BUSINESS_MANAGER
                && role != UserRole.COMMITTEE_STAFF) {
            throw new SecurityException(
                "You do not have permission to record sales."
            );
        }
    }
}