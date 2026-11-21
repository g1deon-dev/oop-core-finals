/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.service;

/**
 *
 * @author g1deon-dev
 */

import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.security.AuthorizationService;

import java.util.ArrayList;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();
    private final AuthorizationService authorizationService;

    public void addProduct(Product product) throws SQLException {
        authorizationService.requireProductManagementAccess();
        
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (product.getProductName() == null ||
            product.getProductName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }

        if (product.getPriceCentavos() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        if (product.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }

        if (product.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low-stock threshold cannot be negative.");
        }

        String name = product.getProductName().trim();

        if (productDAO.productNameExists(name)) {
            throw new IllegalArgumentException("A product with this name already exists.");
        }

        Product cleanedProduct = new Product(
            0,
            name,
            product.getPriceCentavos(),
            product.getQuantity(),
            product.getLowStockThreshold(),
            true
        );

        productDAO.addProduct(cleanedProduct);
    }

    public List<Product> getAllProducts() throws SQLException {
        authorizationService.requireProductManagementAccess();
        return productDAO.getAllProducts();
    }

    public List<Product> getLowStockProducts() throws SQLException {
        authorizationService.requireProductManagementAccess();

        List<Product> lowStockProducts = new ArrayList<>();

        for(Product product : productDAO.getAllProducts()) {

            if(product.getQuantity() <= product.getLowStockThreshold()) {
                lowStockProducts.add(product);
            }
        }

        return lowStockProducts;

    }

    public List<Product> searchProducts(String keyword) throws SQLException {
        authorizationService.requireProductManagementAccess();

        if(keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }

        return productDAO.searchProducts(keyword.trim());
    }

    
    public void updateProduct(Product product) throws SQLException {
        authorizationService.requireProductManagementAccess();

        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (product.getProductId() <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        if (product.getProductName() == null ||
            product.getProductName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }

        if (product.getPriceCentavos() < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        if (product.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low-stock threshold cannot be negative.");
        }

        String name = product.getProductName().trim();

        if (productDAO.productNameExistsForOtherProduct(
                name, product.getProductId())) {
            throw new IllegalArgumentException("Another product already uses this name.");
        }

        Product cleanedProduct = new Product(
            product.getProductId(),
            name,
            product.getPriceCentavos(),
            product.getQuantity(),
            product.getLowStockThreshold(),
            product.isActive()
        );

        productDAO.updateProduct(cleanedProduct);
    }
        
    public void deactivateProduct(int productId) throws SQLException {
        authorizationService.requireProductManagementAccess();
        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        productDAO.deactivateProduct(productId);
    }

    public List<Product> getInactiveProducts() throws SQLException {
        authorizationService.requireProductManagementAccess();
        return productDAO.getInactiveProducts();
    }

    public void reactivateProduct(int productId) throws SQLException {
        authorizationService.requireProductManagementAccess();
        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        productDAO.reactivateProduct(productId);
    }

    public ProductService(AuthorizationService authorizationService) {
        if (authorizationService == null) {
            throw new IllegalArgumentException(
                "Authorization service cannot be null."
            );
        }

        this.authorizationService = authorizationService;
    }
}
