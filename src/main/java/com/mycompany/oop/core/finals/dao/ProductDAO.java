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
        
        String sql = """
            SELECT * FROM products
            WHERE is_active = TRUE
            ORDER BY product_id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {

                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("price_centavos"),
                    rs.getInt("quantity"),
                    rs.getInt("low_stock_threshold"),
                    rs.getBoolean("is_active")
                );

                products.add(product);
            }
        }
        
        return products;
        
    
    }
    
    public List<Product> searchProducts(String keyword) throws SQLException {
        List<Product> products = new ArrayList<>();

        String sql = """
        SELECT * FROM products
        WHERE product_name LIKE ?
        AND is_active = TRUE
        ORDER BY product_name
        """;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getInt("price_centavos"),
                        rs.getInt("quantity"),
                        rs.getInt("low_stock_threshold"),
                        rs.getBoolean("is_active")
                    );

                    products.add(product);
                }
            }

        }

        return products;
    }

    public void updateProduct(Product product) throws SQLException {

        String sql = """
            UPDATE products
            SET product_name = ?,
                price_centavos = ?,
                low_stock_threshold = ?
            WHERE product_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setInt(2, product.getPriceCentavos());
            stmt.setInt(3, product.getLowStockThreshold());
            stmt.setInt(4, product.getProductId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Product not found.");
            }
        }
    }
        public void deactivateProduct(int productId) throws SQLException {

    String sql = """
        UPDATE products
        SET is_active = FALSE
        WHERE product_id = ?
        """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, productId);

        int rowsUpdated = stmt.executeUpdate();

        if (rowsUpdated == 0) {
            throw new SQLException("Product not found.");
        }
    }
    }

    public List<Product> getInactiveProducts() throws SQLException {
        List<Product> products = new ArrayList<>();

        String sql = """
            SELECT * FROM products
            WHERE is_active = FALSE
            ORDER BY product_id
            """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("price_centavos"),
                    rs.getInt("quantity"),
                    rs.getInt("low_stock_threshold"),
                    rs.getBoolean("is_active")
                );

                products.add(product);
            }
        }

        return products;
    }

    public void reactivateProduct(int productId) throws SQLException {
        String sql = """
            UPDATE products
            SET is_active = TRUE
            WHERE product_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Inactive product not found.");
            }
        }
    }

    public boolean productNameExists(String productName) throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM products
            WHERE normalized_name = LOWER(TRIM(?))
            """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public boolean productNameExistsForOtherProduct(String productName, int productId) throws SQLException {

        String sql = """
            SELECT COUNT(*)
            FROM products
            WHERE normalized_name = LOWER(TRIM(?))
            AND product_id != ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productName);
            stmt.setInt(2, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }   


}
