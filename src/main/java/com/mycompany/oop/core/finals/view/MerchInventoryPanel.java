/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.ProductSortField;
import com.mycompany.oop.core.finals.model.SortDirection;
import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.model.StockStatus;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.InventoryService;
import com.mycompany.oop.core.finals.service.ProductService;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Admin
 */
public class MerchInventoryPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MerchInventoryPanel.class.getName());

    private UserSession session;
    private AuthorizationService authService;
    private ProductService productService;
    private InventoryService inventoryService;
    private final javax.swing.JButton btnAuditLog = new javax.swing.JButton("Audit Log");
    private final JComboBox<String> cmbStockFilter = new JComboBox<>(new String[]{"All Statuses", "IN STOCK", "LOW STOCK", "OUT OF STOCK"});
    private final JComboBox<String> cmbSortField = new JComboBox<>(new String[]{"Default Order", "Name", "Price", "Quantity"});
    private final JComboBox<String> cmbSortDir = new JComboBox<>(new String[]{"ASC", "DESC"});

    /**
     * Creates new form MerchInventoryPanel
     */
    public MerchInventoryPanel() {
        this(null);
    }

    public MerchInventoryPanel(UserSession session) {
        initComponents();
        initBackend(session);
    }

    private void initBackend(UserSession session) {
        polishLayout();
        this.session = session;
        setupListeners();

        if (session == null || !session.isLoggedIn()) {
            return;
        }

        this.authService = new AuthorizationService(session);
        this.productService = new ProductService(authService);
        this.inventoryService = new InventoryService(authService, session);

        lblUsername.setText(session.getCurrentUser().getFullName());
        lblUsername1.setText(session.getCurrentUser().getRole().name());

        btnAddProduct.setEnabled(true);
        btnEdit.setEnabled(true);
        btnDeactivate.setEnabled(true);

        refreshData();
    }

    private void setupListeners() {
        btnSearch.addActionListener(e -> searchProducts());
        txtSearch.addActionListener(e -> searchProducts());
        cmbStockFilter.addActionListener(e -> applyFiltersAndSort());
        cmbSortField.addActionListener(e -> applyFiltersAndSort());
        cmbSortDir.addActionListener(e -> applyFiltersAndSort());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cmbStockFilter.setSelectedIndex(0);
            cmbSortField.setSelectedIndex(0);
            cmbSortDir.setSelectedIndex(0);
            refreshData();
        });
        btnAddProduct.addActionListener(e -> handleAddProduct());
        btnEdit.addActionListener(e -> handleEditProduct());
        btnAuditLog.addActionListener(e -> handleViewAllAuditLogs());
        btnMerchInventory.addActionListener(e -> refreshData());
        btnRentalService.addActionListener(e -> {
            new RentalServicePanel(session).setVisible(true);
            this.dispose();
        });

        setupTableContextMenu();
    }

    private void setupTableContextMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem miRestockOrRequest = new JMenuItem("Adjust Stock (+/-)");
        miRestockOrRequest.addActionListener(e -> handleRestockOrRequest());

        JMenuItem miMovements = new JMenuItem("View Stock Movement History");
        miMovements.addActionListener(e -> handleViewStockMovements());

        JMenuItem miReactivate = new JMenuItem("View / Reactivate Inactive Products");
        miReactivate.addActionListener(e -> handleReactivateProducts());

        menu.add(miRestockOrRequest);
        menu.add(miMovements);
        menu.addSeparator();
        menu.add(miReactivate);

        tblProducts.addMouseListener(new MouseAdapter() {
            private void maybeShowPopup(MouseEvent e) {
                int row = tblProducts.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    tblProducts.setRowSelectionInterval(row, row);
                }
                if (e.isPopupTrigger()) {
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblProducts.getSelectedRow() >= 0) {
                    handleViewStockMovements();
                }
            }
        });
    }

    /**
     * Cleans up alignment and screen-fit without altering NetBeans' initComponents().
     */
    private void polishLayout() {
        jLabel2.setText("");

        // ── Sidebar ──────────────────────────────────────────────────────────
        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.removeAll();
        pnlSidebar.setLayout(sidebarLayout);

        javax.swing.border.Border navPadding = javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12);
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setBorder(navPadding);
        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.setBorder(navPadding);
        btnRentalService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRentalService.setBorder(navPadding);
        btnMerchInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMerchInventory.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 3, 0, 0, new java.awt.Color(192, 210, 224)),
            javax.swing.BorderFactory.createEmptyBorder(0, 9, 0, 12)
        ));
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setBorder(navPadding);

        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMerchInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPromos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRentalService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogout, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMerchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRentalService, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        // ── Summary cards ─────────────────────────────────────────────────────
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // ── Table renderers ───────────────────────────────────────────────────
        // Center renderer (ID, Price, Qty, Threshold)
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Left-padded renderer for Product Name
        javax.swing.table.DefaultTableCellRenderer nameRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                return this;
            }
        };

        // Colored stock status renderer
        javax.swing.table.DefaultTableCellRenderer statusRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                if (!isSelected) {
                    String v = value == null ? "" : value.toString();
                    if ("OUT OF STOCK".equals(v)) {
                        setForeground(new java.awt.Color(200, 30, 30));
                        setFont(getFont().deriveFont(java.awt.Font.BOLD));
                    } else if ("LOW STOCK".equals(v)) {
                        setForeground(new java.awt.Color(190, 100, 0));
                        setFont(getFont().deriveFont(java.awt.Font.BOLD));
                    } else {
                        setForeground(new java.awt.Color(0, 140, 60));
                        setFont(getFont().deriveFont(java.awt.Font.BOLD));
                    }
                } else {
                    setForeground(table.getSelectionForeground());
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                }
                return this;
            }
        };

        // Override Integer default renderer so numeric cols are centered, not right-aligned
        tblProducts.setDefaultRenderer(Integer.class, centerRenderer);
        tblProducts.setDefaultRenderer(Object.class, centerRenderer);

        tblProducts.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tblProducts.setDefaultEditor(Object.class, null);
        tblProducts.setDefaultEditor(Integer.class, null);
        tblProducts.setDefaultEditor(String.class, null);

        // Per-column renderers (applied after setDefaultRenderer so they take priority)
        tblProducts.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);   // Product ID
        tblProducts.getColumnModel().getColumn(1).setCellRenderer(nameRenderer);     // Product Name
        tblProducts.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);   // Price
        tblProducts.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);   // Quantity
        tblProducts.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);   // Low Stock Threshold
        tblProducts.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);   // Stock Status

        // Center all column headers
        javax.swing.table.JTableHeader header = tblProducts.getTableHeader();
        ((javax.swing.table.DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // Set preferred column widths (proportional, table auto-resizes)
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(80);   // Product ID
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(220);  // Product Name
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(110);  // Price
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(80);   // Quantity
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(140);  // Low Stock Threshold
        tblProducts.getColumnModel().getColumn(5).setPreferredWidth(110);  // Stock Status

        // ── Product Management toolbar: add Audit Log and Filter/Sort controls ──
        btnAuditLog.setBackground(new java.awt.Color(191, 205, 221));
        btnAuditLog.setFont(new java.awt.Font("Segoe UI Historic", java.awt.Font.BOLD, 12));
        btnAuditLog.setForeground(new java.awt.Color(9, 30, 57));
        btnAuditLog.setFocusPainted(false);
        btnAuditLog.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        javax.swing.GroupLayout toolbarLayout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(toolbarLayout);
        toolbarLayout.setHorizontalGroup(
            toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblProductManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(toolbarLayout.createSequentialGroup()
                        .addComponent(btnAddProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeactivate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAuditLog))
                    .addGroup(toolbarLayout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbStockFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSortField, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSortDir, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        toolbarLayout.setVerticalGroup(
            toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(toolbarLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblProductManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStockFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSortField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbSortDir, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(toolbarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddProduct)
                    .addComponent(btnEdit)
                    .addComponent(btnDeactivate)
                    .addComponent(btnRefresh)
                    .addComponent(btnAuditLog))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        // ── Scroll wrapper for smaller screens ────────────────────────────────
        javax.swing.JScrollPane mainScroll = new javax.swing.JScrollPane(pnlMain);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(pnlSidebar, java.awt.BorderLayout.WEST);
        getContentPane().add(mainScroll, java.awt.BorderLayout.CENTER);

        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1280, screen.width - 40);
        int height = Math.min(800, screen.height - 60);
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void refreshData() {
        if (!ensureConnected()) {
            return;
        }
        try {
            List<Product> products = productService.getAllProducts();
            populateTable(products);
            jLabel3.setText(String.valueOf(inventoryService.getTotalProductCount()));
            jLabel9.setText(String.valueOf(inventoryService.getTotalStockUnits()));
            jLabel11.setText(String.valueOf(inventoryService.getLowStockCount()));
        } catch (SecurityException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void searchProducts() {
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        if (!ensureConnected()) {
            return;
        }
        String keyword = txtSearch.getText();
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        StockStatus status = switch (cmbStockFilter.getSelectedIndex()) {
            case 1 -> StockStatus.IN_STOCK;
            case 2 -> StockStatus.LOW_STOCK;
            case 3 -> StockStatus.OUT_OF_STOCK;
            default -> null;
        };

        ProductSortField sortField = switch (cmbSortField.getSelectedIndex()) {
            case 1 -> ProductSortField.NAME;
            case 2 -> ProductSortField.PRICE;
            case 3 -> ProductSortField.QUANTITY;
            default -> null;
        };
        SortDirection sortDir = cmbSortDir.getSelectedIndex() == 1 ? SortDirection.DESCENDING : SortDirection.ASCENDING;

        try {
            List<Product> results;
            if (hasKeyword && status != null) {
                ProductSortField effectiveSort = sortField != null ? sortField : ProductSortField.NAME;
                results = productService.searchFilterAndSortProducts(keyword, status, effectiveSort, sortDir);
            } else if (hasKeyword) {
                results = productService.searchProducts(keyword);
            } else if (status != null && sortField != null) {
                results = productService.getProductsByStockStatusSorted(status, sortField, sortDir);
            } else if (status != null) {
                results = productService.getProductsByStockStatus(status);
            } else if (sortField != null) {
                results = productService.getSortedProducts(sortField, sortDir);
            } else {
                results = productService.getAllProducts();
            }
            populateTable(results);
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleAddProduct() {
        if (!ensureConnected()) {
            return;
        }
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField qtyField = new JTextField("0");
        JTextField thresholdField = new JTextField("5");

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("Price (PHP):"));
        form.add(priceField);
        form.add(new JLabel("Initial Quantity:"));
        form.add(qtyField);
        form.add(new JLabel("Low Stock Threshold:"));
        form.add(thresholdField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Add Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int priceCentavos = (int) Math.round(Double.parseDouble(priceField.getText().trim()) * 100);
            int quantity = Integer.parseInt(qtyField.getText().trim());
            int threshold = Integer.parseInt(thresholdField.getText().trim());

            inventoryService.addProductWithAudit(new Product(0, nameField.getText(), priceCentavos, quantity, threshold, true));
            refreshData();
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Merch Inventory", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            showError("Please enter valid numbers for price, quantity, and threshold.");
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleEditProduct() {
        if (!ensureConnected()) {
            return;
        }
        Integer productId = getSelectedProductId();
        if (productId == null) {
            return;
        }

        try {
            Product existing = productService.getProductById(productId);
            if (existing == null) {
                showError("Selected product was not found.");
                return;
            }

            JTextField nameField = new JTextField(existing.getProductName());
            JTextField priceField = new JTextField(String.format("%.2f", existing.getPriceCentavos() / 100.0));
            JTextField thresholdField = new JTextField(String.valueOf(existing.getLowStockThreshold()));
            JTextField adjustField = new JTextField("0");
            JTextField reasonField = new JTextField();

            JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
            form.add(new JLabel("Product Name:"));
            form.add(nameField);
            form.add(new JLabel("Price (PHP):"));
            form.add(priceField);
            form.add(new JLabel("Low Stock Threshold:"));
            form.add(thresholdField);
            form.add(new JLabel("Adjust Stock (+/- Qty, e.g. 5 or -3):"));
            form.add(adjustField);
            form.add(new JLabel("Adjustment Reason (optional):"));
            form.add(reasonField);

            int result = JOptionPane.showConfirmDialog(
                this, form, "Edit Product #" + productId + " (Current Qty: " + existing.getQuantity() + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            int priceCentavos = (int) Math.round(Double.parseDouble(priceField.getText().trim()) * 100);
            int threshold = Integer.parseInt(thresholdField.getText().trim());
            int adjustQty = Integer.parseInt(adjustField.getText().trim());

            productService.updateProduct(new Product(
                existing.getProductId(),
                nameField.getText(),
                priceCentavos,
                existing.getQuantity(),
                threshold,
                existing.isActive()
            ));
            if (adjustQty != 0) {
                inventoryService.adjustStock(productId, adjustQty, reasonField.getText());
            }
            refreshData();
            JOptionPane.showMessageDialog(this, "Product updated successfully!", "Merch Inventory", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            showError("Please enter valid numbers for price, threshold, and stock adjustment.");
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleRestockOrRequest() {
        if (!ensureConnected()) {
            return;
        }
        Integer productId = getSelectedProductId();
        if (productId == null) {
            return;
        }

        JTextField qtyField = new JTextField("1");
        JTextField reasonField = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Adjust Qty (+ add / - subtract):"));
        form.add(qtyField);
        form.add(new JLabel("Reason (optional):"));
        form.add(reasonField);

        String title = "Adjust Stock — Product #" + productId;
        int result = JOptionPane.showConfirmDialog(this, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int qty = Integer.parseInt(qtyField.getText().trim());
            inventoryService.adjustStock(productId, qty, reasonField.getText());
            refreshData();
            String verb = qty > 0 ? ("Added +" + qty) : ("Deducted " + qty);
            JOptionPane.showMessageDialog(this, verb + " units.", "Stock Updated", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException nfe) {
            showError("Quantity must be a valid integer.");
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleViewStockMovements() {
        if (!ensureConnected()) {
            return;
        }
        Integer productId = getSelectedProductId();
        if (productId == null) {
            return;
        }
        try {
            List<StockMovement> movements = inventoryService.getMovementsByProductId(productId);
            if (movements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No stock movements recorded for Product #" + productId + ".", "Stock Movement History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Change", "Reason", "Ref ID", "User ID", "Timestamp"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (StockMovement m : movements) {
                model.addRow(new Object[]{
                    m.getMovementId(),
                    (m.getChangeAmount() > 0 ? "+" : "") + m.getChangeAmount(),
                    m.getReason(),
                    m.getReferenceId() == null ? "-" : m.getReferenceId(),
                    m.getCreatedBy(),
                    m.getCreatedAt() == null ? "-" : m.getCreatedAt().toString()
                });
            }
            JTable table = new JTable(model);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(640, 260));
            JOptionPane.showMessageDialog(this, scroll, "Stock Movements — Product #" + productId, JOptionPane.PLAIN_MESSAGE);
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleViewAllAuditLogs() {
        if (!ensureConnected()) {
            return;
        }
        try {
            List<StockMovement> movements = inventoryService.getAllMovements();
            if (movements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No audit movements recorded yet.", "Inventory Audit Log", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            DefaultTableModel model = new DefaultTableModel(
                new String[]{"Move ID", "Product ID", "Change", "Reason", "Ref ID", "User ID", "Timestamp"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (StockMovement m : movements) {
                model.addRow(new Object[]{
                    m.getMovementId(),
                    m.getProductId(),
                    (m.getChangeAmount() > 0 ? "+" : "") + m.getChangeAmount(),
                    m.getReason(),
                    m.getReferenceId() == null ? "-" : m.getReferenceId(),
                    m.getCreatedBy(),
                    m.getCreatedAt() == null ? "-" : m.getCreatedAt().toString()
                });
            }
            JTable table = new JTable(model);
            table.setRowHeight(26);
            JScrollPane scroll = new JScrollPane(table);
            scroll.setPreferredSize(new Dimension(720, 340));
            JOptionPane.showMessageDialog(this, scroll, "Inventory Audit Log (All Stock Movements)", JOptionPane.PLAIN_MESSAGE);
        } catch (SecurityException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleReactivateProducts() {
        if (!ensureConnected()) {
            return;
        }
        try {
            List<Product> inactive = productService.getInactiveProducts();
            if (inactive.isEmpty()) {
                JOptionPane.showMessageDialog(this, "There are no inactive products.", "Inactive Products", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] choices = new String[inactive.size()];
            for (int i = 0; i < inactive.size(); i++) {
                Product p = inactive.get(i);
                choices[i] = "#" + p.getProductId() + " — " + p.getProductName() + " (Qty: " + p.getQuantity() + ")";
            }
            Object selected = JOptionPane.showInputDialog(
                this,
                "Select a product to reactivate:",
                "Reactivate Product",
                JOptionPane.PLAIN_MESSAGE,
                null,
                choices,
                choices[0]
            );
            if (selected == null) {
                return;
            }
            String selStr = selected.toString();
            int id = Integer.parseInt(selStr.substring(1, selStr.indexOf(" ")));
            inventoryService.reactivateProductWithAudit(id);
            refreshData();
            JOptionPane.showMessageDialog(this, "Product #" + id + " reactivated.", "Reactivate", JOptionPane.INFORMATION_MESSAGE);
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }

    private void populateTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0);
        for (Product p : products) {
            model.addRow(new Object[]{
                p.getProductId(),
                p.getProductName(),
                String.format("PHP %,.2f", p.getPriceCentavos() / 100.0),
                p.getQuantity(),
                p.getLowStockThreshold(),
                computeStockStatus(p)
            });
        }
    }

    private String computeStockStatus(Product p) {
        if (p.getQuantity() <= 0) {
            return "OUT OF STOCK";
        }
        if (p.getQuantity() <= p.getLowStockThreshold()) {
            return "LOW STOCK";
        }
        return "IN STOCK";
    }

    private Integer getSelectedProductId() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            showError("Please select a product row first.");
            return null;
        }
        Object val = tblProducts.getValueAt(row, 0);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        showError("Invalid product row selected.");
        return null;
    }

    private boolean ensureConnected() {
        if (session == null || !session.isLoggedIn() || productService == null) {
            showError("No active user session. Open this screen with a logged-in UserSession.");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Merch Inventory", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnlSidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnDashboard = new javax.swing.JButton();
        btnMerchInventory = new javax.swing.JButton();
        btnPromos = new javax.swing.JButton();
        btnRentalService = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblPageTitle = new javax.swing.JLabel();
        lblPageTitleDescription = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        lblUsername1 = new javax.swing.JLabel();
        pnlTotalProducts = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        pnlTotalStock = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblProductManagement = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnAddProduct = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDeactivate = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        pnlLowStock = new javax.swing.JPanel();
        ;
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlSidebar.setBackground(new java.awt.Color(30, 30, 30));
        pnlSidebar.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, java.awt.Color.lightGray));
        pnlSidebar.setForeground(new java.awt.Color(160, 160, 160));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(245, 800));

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 3, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(192, 210, 224));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/mycompany/oop/core/finals/view/logo.png"))); // NOI18N

        btnDashboard.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(245, 245, 245));
        btnDashboard.setText("DASHBOARD");
        btnDashboard.setBorder(null);
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.addActionListener(this::btnDashboardActionPerformed);

        btnMerchInventory.setBackground(new java.awt.Color(11, 52, 90));
        btnMerchInventory.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnMerchInventory.setForeground(new java.awt.Color(192, 210, 224));
        btnMerchInventory.setText("MERCH INVENTORY");
        btnMerchInventory.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnMerchInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnPromos.setBackground(new java.awt.Color(160, 160, 160));
        btnPromos.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnPromos.setForeground(new java.awt.Color(245, 245, 245));
        btnPromos.setText("PROMOS");
        btnPromos.setBorder(null);
        btnPromos.setContentAreaFilled(false);
        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.addActionListener(this::btnPromosActionPerformed);

        btnRentalService.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnRentalService.setForeground(new java.awt.Color(245, 245, 245));
        btnRentalService.setText("RENTAL SERVICE");
        btnRentalService.setBorder(null);
        btnRentalService.setContentAreaFilled(false);
        btnRentalService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        btnSettings.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnSettings.setForeground(new java.awt.Color(245, 245, 245));
        btnSettings.setText("Settings");
        btnSettings.setBorder(null);
        btnSettings.setContentAreaFilled(false);
        btnSettings.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnSettings.addActionListener(this::btnSettingsActionPerformed);

        btnLogout.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(192, 210, 224));
        btnLogout.setText("Log Out");
        btnLogout.setBorder(null);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        btnLogout.addActionListener(this::btnLogoutActionPerformed);

        javax.swing.GroupLayout pnlSidebarLayout = new javax.swing.GroupLayout(pnlSidebar);
        pnlSidebar.setLayout(pnlSidebarLayout);
        pnlSidebarLayout.setHorizontalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRentalService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMerchInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlSidebarLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                .addContainerGap(55, Short.MAX_VALUE)
                .addGroup(pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlSidebarLayout.createSequentialGroup()
                        .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))))
        );
        pnlSidebarLayout.setVerticalGroup(
            pnlSidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlSidebarLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMerchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRentalService, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        pnlMain.setBackground(new java.awt.Color(245, 245, 245));
        pnlMain.setPreferredSize(new java.awt.Dimension(1035, 800));

        pnlHeader.setBackground(new java.awt.Color(9, 30, 57));
        pnlHeader.setPreferredSize(new java.awt.Dimension(1035, 130));

        lblPageTitle.setBackground(new java.awt.Color(245, 245, 245));
        lblPageTitle.setFont(new java.awt.Font("Agency FB", 1, 50)); // NOI18N
        lblPageTitle.setForeground(new java.awt.Color(245, 245, 245));
        lblPageTitle.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPageTitle.setText("Merch Inventory");
        lblPageTitle.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblPageTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        lblPageTitle.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lblPageTitle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        lblPageTitleDescription.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblPageTitleDescription.setForeground(new java.awt.Color(245, 245, 245));
        lblPageTitleDescription.setText("Manage your organization's merchandise and stocks");
        lblPageTitleDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setText("jLabel2");

        lblUsername.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(245, 245, 245));
        lblUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsername.setText("Elijah Gideon Yanto");
        lblUsername.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        lblUsername1.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblUsername1.setForeground(new java.awt.Color(245, 245, 245));
        lblUsername1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsername1.setText("Technicals");
        lblUsername1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPageTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPageTitleDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(483, 483, 483)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlHeaderLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUsername1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlHeaderLayout.createSequentialGroup()
                        .addComponent(lblPageTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPageTitleDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlTotalProducts.setBackground(new java.awt.Color(227, 232, 238));
        pnlTotalProducts.setForeground(new java.awt.Color(192, 210, 224));
        pnlTotalProducts.setPreferredSize(new java.awt.Dimension(300, 100));

        jLabel3.setFont(new java.awt.Font("Segoe UI Historic", 1, 52)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 86, 124));
        jLabel3.setText("18");

        jLabel6.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(9, 30, 57));
        jLabel6.setText("Total Products");

        javax.swing.GroupLayout pnlTotalProductsLayout = new javax.swing.GroupLayout(pnlTotalProducts);
        pnlTotalProducts.setLayout(pnlTotalProductsLayout);
        pnlTotalProductsLayout.setHorizontalGroup(
            pnlTotalProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalProductsLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addContainerGap(62, Short.MAX_VALUE))
        );
        pnlTotalProductsLayout.setVerticalGroup(
            pnlTotalProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalProductsLayout.createSequentialGroup()
                .addGroup(pnlTotalProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTotalProductsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTotalProductsLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pnlTotalStock.setBackground(new java.awt.Color(191, 205, 221));
        pnlTotalStock.setForeground(new java.awt.Color(192, 210, 224));
        pnlTotalStock.setPreferredSize(new java.awt.Dimension(300, 100));

        jLabel9.setFont(new java.awt.Font("Segoe UI Historic", 1, 52)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(33, 86, 124));
        jLabel9.setText("127");

        jLabel10.setBackground(new java.awt.Color(222, 239, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(9, 30, 57));
        jLabel10.setText("Total Stocks");

        javax.swing.GroupLayout pnlTotalStockLayout = new javax.swing.GroupLayout(pnlTotalStock);
        pnlTotalStock.setLayout(pnlTotalStockLayout);
        pnlTotalStockLayout.setHorizontalGroup(
            pnlTotalStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalStockLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        pnlTotalStockLayout.setVerticalGroup(
            pnlTotalStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTotalStockLayout.createSequentialGroup()
                .addGroup(pnlTotalStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlTotalStockLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlTotalStockLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(227, 232, 238));

        lblProductManagement.setFont(new java.awt.Font("Segoe UI Historic", 1, 24)); // NOI18N
        lblProductManagement.setForeground(new java.awt.Color(30, 30, 30));
        lblProductManagement.setText("Product Management");

        txtSearch.setBackground(new java.awt.Color(245, 245, 245));

        btnSearch.setBackground(new java.awt.Color(163, 190, 211));
        btnSearch.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(76, 129, 163));
        btnSearch.setText("Search");

        btnAddProduct.setBackground(new java.awt.Color(218, 245, 228));
        btnAddProduct.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnAddProduct.setForeground(new java.awt.Color(13, 13, 13));
        btnAddProduct.setText("+ Add Product");

        btnEdit.setBackground(new java.awt.Color(222, 239, 255));
        btnEdit.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(2, 133, 255));
        btnEdit.setText("Edit");

        btnDeactivate.setBackground(new java.awt.Color(254, 225, 224));
        btnDeactivate.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnDeactivate.setForeground(new java.awt.Color(224, 46, 42));
        btnDeactivate.setText("Deactivate");
        btnDeactivate.addActionListener(this::btnDeactivateActionPerformed);

        btnRefresh.setBackground(new java.awt.Color(242, 242, 242));
        btnRefresh.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(13, 13, 20));
        btnRefresh.setText("Refresh");
        btnRefresh.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);

        jScrollPane1.setBorder(null);

        tblProducts.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        tblProducts.setForeground(new java.awt.Color(30, 30, 30));
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Product ID", "Product Name", "Price", "Quantity", "Low Stock Threshold", "Stock Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblProducts.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblProducts.setGridColor(new java.awt.Color(224, 230, 235));
        tblProducts.setRowHeight(36);
        tblProducts.setSelectionBackground(new java.awt.Color(205, 235, 250));
        tblProducts.setSelectionForeground(new java.awt.Color(23, 59, 86));
        tblProducts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblProducts.setShowVerticalLines(true);
        jScrollPane1.setViewportView(tblProducts);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblProductManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAddProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeactivate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblProductManagement, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddProduct)
                    .addComponent(btnEdit)
                    .addComponent(btnDeactivate)
                    .addComponent(btnRefresh))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pnlLowStock.setBackground(new java.awt.Color(227, 232, 238));
        pnlLowStock.setForeground(new java.awt.Color(192, 210, 224));
        pnlLowStock.setPreferredSize(new java.awt.Dimension(300, 100));

        jLabel11.setFont(new java.awt.Font("Segoe UI Historic", 1, 52)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(33, 86, 124));
        jLabel11.setText("8");

        jLabel12.setBackground(new java.awt.Color(222, 239, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(9, 30, 57));
        jLabel12.setText("Low Stocks");

        javax.swing.GroupLayout pnlLowStockLayout = new javax.swing.GroupLayout(pnlLowStock);
        pnlLowStock.setLayout(pnlLowStockLayout);
        pnlLowStockLayout.setHorizontalGroup(
            pnlLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLowStockLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addContainerGap(124, Short.MAX_VALUE))
        );
        pnlLowStockLayout.setVerticalGroup(
            pnlLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLowStockLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlLowStockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlLowStockLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(pnlTotalStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(pnlLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlTotalStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(31, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlSidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlSidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        if (session == null || !session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "No active user session.", "Settings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        UserRole role = session.getCurrentUser().getRole();
        boolean isAdmin = (role == UserRole.CHAIRPERSON || role == UserRole.BUSINESS_MANAGER);
        String info = "User: " + session.getCurrentUser().getFullName()
            + "\nUsername: " + session.getCurrentUser().getUsername()
            + "\nRole: " + role.name()
            + "\nAccess Level: " + (isAdmin ? "Admin (Full Inventory & Approval Access)" : "Staff (View, Search, Sales, Request)")
            + "\nDatabase: Supabase PostgreSQL (Connected)";

        if (role == UserRole.CHAIRPERSON) {
            Object[] options = {"Create New Account", "Close"};
            int choice = JOptionPane.showOptionDialog(
                this, info, "Account & System Info",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[1]
            );
            if (choice == 0) {
                handleCreateUserAccount();
            }
        } else {
            JOptionPane.showMessageDialog(this, info, "Account & System Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void handleCreateUserAccount() {
        JTextField usernameField = new JTextField();
        JTextField fullNameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<UserRole> roleBox = new JComboBox<>(new UserRole[]{UserRole.CHAIRPERSON, UserRole.BUSINESS_MANAGER});

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Full Name:"));
        form.add(fullNameField);
        form.add(new JLabel("Password (min 8 chars):"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        int result = JOptionPane.showConfirmDialog(
            this, form, "Create User Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            AuthService authService = new AuthService(session);
            authService.createUser(
                usernameField.getText(),
                fullNameField.getText(),
                new String(passwordField.getPassword()),
                (UserRole) roleBox.getSelectedItem()
            );
            JOptionPane.showMessageDialog(this, "Account created successfully!", "User Management", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out?",
            "Log Out",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        if (session != null) {
            session.endSession();
        }
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnPromosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPromosActionPerformed
        new PromosPanel(session).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnPromosActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        new DashboardPanel(session).setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnDeactivateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateActionPerformed
        if (!ensureConnected()) {
            return;
        }
        Integer productId = getSelectedProductId();
        if (productId == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deactivate product #" + productId + "?",
            "Confirm Deactivate",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            inventoryService.deactivateProductWithAudit(productId);
            refreshData();
            JOptionPane.showMessageDialog(this, "Product #" + productId + " deactivated.", "Merch Inventory", JOptionPane.INFORMATION_MESSAGE);
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            showError(ex.getMessage());
        }
    }//GEN-LAST:event_btnDeactivateActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MerchInventoryPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddProduct;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeactivate;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMerchInventory;
    private javax.swing.JButton btnPromos;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRentalService;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblPageTitle;
    private javax.swing.JLabel lblPageTitleDescription;
    private javax.swing.JLabel lblProductManagement;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel lblUsername1;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlLowStock;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSidebar;
    private javax.swing.JPanel pnlTotalProducts;
    private javax.swing.JPanel pnlTotalStock;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
