/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.dao;

/**
 *
 * @author g1deon-dev
 */

import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    public void addProduct(Product product) throws SQLException {
    
        String sql = """
                     INSERT INTO products
                     (product_name, price_centavos, quantity, low_stock_threshold)
                     VALUES (?,?,?,?)
                     """;
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getPriceCentavos());
            stmt.setInt(3, product.getQuantity());
            stmt.setInt(4, product.getLowStockThreshold());
            
            stmt.executeUpdate();
        }
    }
    
    public List<Product> getAllProducts() throws SQLException {
    
        List<Product> products = new ArrayList<>();
        
        String sql = "SELECT * FROM products ORDER BY product_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {

                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("price_centavos"),
                    rs.getInt("quantity"),
                    rs.getInt("low_stock_threshold")
                );

                products.add(product);
            }
        }
        
        return products;
        
    
    }
    
    
}
