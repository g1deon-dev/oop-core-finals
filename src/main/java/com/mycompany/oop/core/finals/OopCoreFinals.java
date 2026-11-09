/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.oop.core.finals;

/**
 *
 * @author g1deon-dev
 */

import com.mycompany.oop.core.finals.util.DatabaseConnection;

public class OopCoreFinals {

    public static void main(String[] args) {
        try (var conn = DatabaseConnection.getConnection()) {

        System.out.println("MySQL Connection Successful!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
