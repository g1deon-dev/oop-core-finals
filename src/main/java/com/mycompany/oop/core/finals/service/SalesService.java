
package com.mycompany.oop.core.finals.service;

import com.mycompany.oop.core.finals.dao.SaleDAO;
import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.SaleItem;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesService {

    private final SaleDAO saleDAO;
    private final AuthorizationService authorizationService;
    private final UserSession session;

    public SalesService(
            AuthorizationService authorizationService,
            UserSession session) {

        this.saleDAO = new SaleDAO();
        this.authorizationService = authorizationService;
        this.session = session;
    }

    // Records a sale containing products, bundles, or both.
    public long recordSale(List<SaleItem> items)
            throws SQLException {

        authorizationService.requireSalesAccess();

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException(
                "A sale must contain at least one item."
            );
        }

        List<SaleItem> validatedItems = new ArrayList<>();

        for (SaleItem item : items) {

            if (item == null) {
                throw new IllegalArgumentException(
                    "Sale contains an invalid item."
                );
            }

            boolean validProduct =
                item.getProductId() > 0
                && item.getBundleId() == 0;

            boolean validBundle =
                item.getBundleId() > 0
                && item.getProductId() == 0;

            if (!validProduct && !validBundle) {
                throw new IllegalArgumentException(
                    "A sale item must represent exactly one "
                    + "product or bundle."
                );
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                    "Item quantity must be greater than zero."
                );
            }

            validatedItems.add(item);
        }

        int userId = session.getCurrentUser().getUserId();

        Sale sale = new Sale(userId, validatedItems);

        return saleDAO.createSale(sale);
    }

    // Retrieves a sale by its ID.
    public Sale getSaleById(long saleId)
            throws SQLException {

        authorizationService.requireSalesAccess();

        if (saleId <= 0) {
            throw new IllegalArgumentException(
                "Invalid sale ID."
            );
        }

        return saleDAO.getSaleById(saleId);
    }

    // Retrieves the complete sales history.
    public List<Sale> getAllSales()
            throws SQLException {

        authorizationService.requireSalesAccess();

        return saleDAO.getAllSales();
    }
}
