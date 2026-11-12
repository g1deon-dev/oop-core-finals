/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.oop.core.finals;

/**
 *
 * @author g1deon-dev
 */

import com.mycompany.oop.core.finals.util.DatabaseConnection;
import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.model.Product;
import java.sql.SQLException;


public class OopCoreFinals {

    public static void main(String[] args) {
            try{
                ProductDAO dao = new ProductDAO();

                Product product = new Product(0, "Org Shirt", 25000, 20, 5);

                if(dao.getAllProducts().isEmpty()) {
                    dao.addProduct(product);
                    System.out.println("Product added successfully!");
                }

                for(Product p : dao.getAllProducts()) {
                    System.out.println(p.getProductId() + " | " + p.getProductName() +" | "+ p.getQuantity() );
                }
            
            } catch (SQLException error) {
                System.out.println("Database error: " + error.getMessage());
                error.printStackTrace();
            }
        
    }
    
    
}
