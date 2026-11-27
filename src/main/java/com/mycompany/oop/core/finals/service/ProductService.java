/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oop.core.finals.service;

/**
 *
 * @author g1deon-dev
 */

import com.mycompany.oop.core.finals.model.StockStatus;
import com.mycompany.oop.core.finals.model.ProductSortField;
import com.mycompany.oop.core.finals.model.SortDirection;
import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.StockStatus;
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
        authorizationService.requireProductViewAccess();
        return productDAO.getAllProducts();
    }

    public Product getProductById(int productId) throws SQLException {
        authorizationService.requireProductViewAccess();

        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        return productDAO.getProductById(productId);
    }

    public List<Product> getLowStockProducts() throws SQLException {
        authorizationService.requireProductViewAccess();

        List<Product> lowStockProducts = new ArrayList<>();

        for(Product product : productDAO.getAllProducts()) {

            if(product.getQuantity() <= product.getLowStockThreshold()) {
                lowStockProducts.add(product);
            }
        }

        return lowStockProducts;

    }

    public List<Product> searchProducts(String keyword) throws SQLException {
        authorizationService.requireProductViewAccess();

        if(keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }

        return productDAO.searchProducts(keyword.trim());
    }

    public void restockProduct(int productId, int quantityToAdd) throws SQLException {
        authorizationService.requireProductManagementAccess();

        if (productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID.");
        }

        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero.");
        }

        productDAO.restockProduct(productId, quantityToAdd);
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

    public List<Product> getSortedProducts(
            ProductSortField sortField,
            SortDirection sortDirection) throws SQLException {

        authorizationService.requireProductViewAccess();

        if (sortField == null) {
            throw new IllegalArgumentException(
                "Sort field is required."
            );
        }

        if (sortDirection == null) {
            throw new IllegalArgumentException(
                "Sort direction is required."
            );
        }

        return productDAO.getSortedProducts(sortField, sortDirection);
    }
    public List<Product> getProductsByStockStatus(StockStatus status) throws SQLException {

        authorizationService.requireProductViewAccess();

        if (status == null) {
            throw new IllegalArgumentException(
                "Stock status is required."
            );
        }

        List<Product> filteredProducts = new ArrayList<>();

        for (Product product : productDAO.getAllProducts()) {

            int quantity = product.getQuantity();
            int threshold = product.getLowStockThreshold();

            switch (status) {
                case IN_STOCK -> {
                    if (quantity > threshold) {
                        filteredProducts.add(product);
                    }
                }

                case LOW_STOCK -> {
                    if (quantity > 0 && quantity <= threshold) {
                        filteredProducts.add(product);
                    }
                }

                case OUT_OF_STOCK -> {
                    if (quantity == 0) {
                        filteredProducts.add(product);
                    }
                }
            }
        }

        return filteredProducts;
    }
	public List<Product> getProductsByStockStatusSorted(
	        StockStatus status,
	        ProductSortField sortField,
	        SortDirection sortDirection) throws SQLException {

	    authorizationService.requireProductViewAccess();

	    if (status == null) {
	        throw new IllegalArgumentException(
	            "Stock status is required."
	        );
	    }

	    if (sortField == null) {
	        throw new IllegalArgumentException(
	            "Sort field is required."
	        );
	    }

	    if (sortDirection == null) {
	        throw new IllegalArgumentException(
	            "Sort direction is required."
	        );
	    }

	    return productDAO.getProductsByStockStatusSorted(
	        status, sortField, sortDirection
	    );
	}

    public List<Product> searchFilterAndSortProducts(
            String keyword,
            StockStatus status,
            ProductSortField sortField,
            SortDirection sortDirection) throws SQLException {

        authorizationService.requireProductViewAccess();

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                "Search keyword cannot be empty."
            );
        }

        if (status == null || sortField == null || sortDirection == null) {
            throw new IllegalArgumentException(
                "Stock status, sort field, and direction are required."
            );
        }

        return productDAO.searchFilterAndSortProducts(
            keyword.trim(), status, sortField, sortDirection
        );
    }

}
