
package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.BundleDAO;
import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.BundleItem;
import com.mycompany.oop.core.finals.security.AuthorizationService;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BundleService {

    private final BundleDAO bundleDAO;
    private final AuthorizationService authorizationService;

    public BundleService(AuthorizationService authorizationService) {
        this.bundleDAO = new BundleDAO();
        this.authorizationService = authorizationService;
    }

    public void createBundle(Bundle bundle) throws SQLException {

        authorizationService.requireProductManagementAccess();

        validateBundle(bundle);

        if (bundleDAO.bundleNameExists(bundle.getBundleName())) {
            throw new IllegalArgumentException(
                "A bundle with this name already exists."
            );
        }

        bundleDAO.addBundle(bundle);
    }

    public List<Bundle> getAllBundles() throws SQLException {

        authorizationService.requireLogin();

        return bundleDAO.getAllBundles();
    }

    public Bundle getBundleById(int bundleId) throws SQLException {

        authorizationService.requireLogin();

        if (bundleId <= 0) {
            throw new IllegalArgumentException("Invalid bundle ID.");
        }

        return bundleDAO.getBundleById(bundleId);
    }

    public void updateBundle(Bundle bundle) throws SQLException {

        authorizationService.requireProductManagementAccess();

        validateBundle(bundle);

        if (bundle.getBundleId() <= 0) {
            throw new IllegalArgumentException("Invalid bundle ID.");
        }

        Bundle existing = bundleDAO.getBundleById(
            bundle.getBundleId()
        );

        if (existing == null) {
            throw new IllegalArgumentException("Bundle not found.");
        }

        if (!existing.getBundleName().equalsIgnoreCase(
                bundle.getBundleName().trim())
                && bundleDAO.bundleNameExists(bundle.getBundleName())) {

            throw new IllegalArgumentException(
                "A bundle with this name already exists."
            );
        }

        bundleDAO.updateBundle(bundle);
    }

    public void deactivateBundle(int bundleId) throws SQLException {

        authorizationService.requireProductManagementAccess();

        if (bundleId <= 0) {
            throw new IllegalArgumentException("Invalid bundle ID.");
        }

        bundleDAO.deactivateBundle(bundleId);
    }

    public void reactivateBundle(int bundleId) throws SQLException {

        authorizationService.requireProductManagementAccess();

        if (bundleId <= 0) {
            throw new IllegalArgumentException("Invalid bundle ID.");
        }

        bundleDAO.reactivateBundle(bundleId);
    }

    private void validateBundle(Bundle bundle) {

        if (bundle == null) {
            throw new IllegalArgumentException("Bundle cannot be null.");
        }

        if (bundle.getBundleName() == null
                || bundle.getBundleName().trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Bundle name cannot be empty."
            );
        }

        if (bundle.getPriceCentavos() < 0) {
            throw new IllegalArgumentException(
                "Bundle price cannot be negative."
            );
        }

        List<BundleItem> items = bundle.getItems();

        if (items.isEmpty()) {
            throw new IllegalArgumentException(
                "A bundle must contain at least one product."
            );
        }

        Set<Integer> productIds = new HashSet<>();

        for (BundleItem item : items) {

            if (item == null || item.getProductId() <= 0) {
                throw new IllegalArgumentException(
                    "Invalid product in bundle."
                );
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                    "Component quantity must be greater than zero."
                );
            }

            if (!productIds.add(item.getProductId())) {
                throw new IllegalArgumentException(
                    "A product cannot appear twice in the same bundle."
                );
            }
        }
    }
}
