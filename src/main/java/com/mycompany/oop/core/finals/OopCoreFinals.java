package com.mycompany.oop.core.finals;

import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;

import java.util.Scanner;

public class OopCoreFinals {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);

        try {
            System.out.println("================================");
            System.out.println(" STASH - Organization Management");
            System.out.println("================================");

            System.out.println("\n=== LOGIN ===");

            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authService.login(username, password);

            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + user.getFullName());
            System.out.println("Role: " + user.getRole());

            System.out.println("\nSTASH is running.");
            System.out.println("Press Enter to log out.");

            scanner.nextLine();

        } catch (Exception e) {

            System.out.println("\nApplication error: " + e.getMessage());

        } finally {

            authService.logout();
            scanner.close();

            System.out.println("\nSession ended.");
        }
    }
}
