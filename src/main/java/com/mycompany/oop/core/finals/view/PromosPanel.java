/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.model.Bundle;
import com.mycompany.oop.core.finals.model.PreOrder;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.BundleService;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.PreOrderService;
import com.mycompany.oop.core.finals.service.ProductService;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class PromosPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PromosPanel.class.getName());

    private UserSession session;
    private AuthorizationService authService;
    private BundleService bundleService;
    private PreOrderService preOrderService;
    private ProductService productService;
    private List<Bundle> loadedBundles = new ArrayList<>();

    /**
     * Creates new form PromosPanel
     */
    public PromosPanel() {
        this(null);
    }

    public PromosPanel(UserSession session) {
        initComponents();
        initBackend(session);
    }

    private void initBackend(UserSession session) {
        this.session = session;
        polishLayout();
        setupListeners();

        if (session == null || !session.isLoggedIn()) {
            return;
        }

        this.authService = new AuthorizationService(session);
        this.bundleService = new BundleService(authService);
        this.preOrderService = new PreOrderService(authService, session);
        this.productService = new ProductService(authService);
        jLabel1.setText(session.getCurrentUser().getRole().name());
        jLabel2.setText(session.getCurrentUser().getFullName());

        loadPromosData();
    }

    private void setupListeners() {
        btnMerchInventory.addActionListener(e -> {
            new MerchInventoryPanel(session).setVisible(true);
            this.dispose();
        });
        btnDashboard.addActionListener(e -> javax.swing.JOptionPane.showMessageDialog(
            this, "Dashboard is not yet implemented.", "Dashboard", javax.swing.JOptionPane.INFORMATION_MESSAGE));
        btnRentalService.addActionListener(e -> javax.swing.JOptionPane.showMessageDialog(
            this, "Rental Service is managed by the Rental team (coming soon).", "Rental Service", javax.swing.JOptionPane.INFORMATION_MESSAGE));
        jButton5.addActionListener(e -> {  // Log Out
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                this, "Are you sure you want to log out?", "Log Out", javax.swing.JOptionPane.YES_NO_OPTION);
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                if (session != null) session.endSession();
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
        jButton6.addActionListener(e -> showAccountSettings());
        btnBundle1.addActionListener(e -> showBundleDetails(0));
        btnBundle3.addActionListener(e -> showBundleDetails(2));
    }

    /**
     * Rebuilds the sidebar so PROMOS is left-aligned (highlighted) rather than right-shifted,
     * and makes the Log Out button clearly visible.
     */
    private void polishLayout() {
        // ── Sidebar button styles ──────────────────────────────────────────────
        javax.swing.border.Border navPadding = javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12);

        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setBorder(navPadding);
        btnDashboard.setMaximumSize(null);
        btnDashboard.setMinimumSize(null);
        btnDashboard.setPreferredSize(null);

        btnMerchInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMerchInventory.setBorder(navPadding);
        btnMerchInventory.setMaximumSize(null);
        btnMerchInventory.setMinimumSize(null);
        btnMerchInventory.setPreferredSize(null);

        // PROMOS = active: left accent bar + navy bg, stays left-aligned
        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 3, 0, 0, new java.awt.Color(192, 210, 224)),
            javax.swing.BorderFactory.createEmptyBorder(0, 9, 0, 12)
        ));
        btnPromos.setMaximumSize(null);
        btnPromos.setMinimumSize(null);
        btnPromos.setPreferredSize(null);
        btnPromos.setOpaque(true);  // needed for bg color to show

        btnRentalService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRentalService.setBorder(navPadding);
        btnRentalService.setMaximumSize(null);
        btnRentalService.setMinimumSize(null);
        btnRentalService.setPreferredSize(null);

        // Settings
        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton6.setBorder(navPadding);
        jButton6.setMaximumSize(null);
        jButton6.setMinimumSize(null);
        jButton6.setPreferredSize(null);

        // Log Out — make it visible: larger font, cyan accent color, border
        jButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton5.setBorder(navPadding);
        jButton5.setMaximumSize(null);
        jButton5.setMinimumSize(null);
        jButton5.setPreferredSize(null);
        jButton5.setFont(new java.awt.Font("Segoe UI Historic", java.awt.Font.PLAIN, 12));
        jButton5.setForeground(new java.awt.Color(192, 210, 224));

        // ── Rebuild sidebar layout (all buttons left-aligned, uniform) ─────────
        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.removeAll();
        jPanel2.setLayout(sidebarLayout);

        // Find the logo label (jLabel component that holds the logo in MerchInventory uses jLabel1,
        // but PromosPanel sidebar has no logo — we keep it empty above nav items)
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMerchInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPromos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRentalService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );
        sidebarLayout.setVerticalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(158, 158, 158)   // same top gap as MerchInventory (logo height + padding)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMerchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRentalService, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );
        rebuildSidebar();
    }

    private void rebuildSidebar() {
        jPanel2.removeAll();
        jPanel2.setLayout(new java.awt.BorderLayout());

        JPanel navigation = new JPanel();
        navigation.setOpaque(false);
        navigation.setLayout(new javax.swing.BoxLayout(navigation, javax.swing.BoxLayout.Y_AXIS));
        navigation.setBorder(javax.swing.BorderFactory.createEmptyBorder(158, 14, 0, 14));
        addSidebarButton(navigation, btnDashboard);
        addSidebarButton(navigation, btnMerchInventory);
        addSidebarButton(navigation, btnPromos);
        addSidebarButton(navigation, btnRentalService);
        jPanel2.add(navigation, java.awt.BorderLayout.NORTH);

        JPanel accountActions = new JPanel();
        accountActions.setOpaque(false);
        accountActions.setLayout(new javax.swing.BoxLayout(accountActions, javax.swing.BoxLayout.Y_AXIS));
        accountActions.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 14, 28, 14));
        addSidebarButton(accountActions, jButton6);
        addSidebarButton(accountActions, jButton5);
        jPanel2.add(accountActions, java.awt.BorderLayout.SOUTH);
    }

    private void addSidebarButton(JPanel container, javax.swing.JButton button) {
        button.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 36));
        button.setPreferredSize(new java.awt.Dimension(210, 36));
        container.add(button);
        container.add(javax.swing.Box.createVerticalStrut(6));
    }

    private void showAccountSettings() {
        if (session == null || !session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "No active user session.", "Settings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        UserRole role = session.getCurrentUser().getRole();
        boolean isAdmin = role == UserRole.CHAIRPERSON || role == UserRole.BUSINESS_MANAGER;
        String info = "User: " + session.getCurrentUser().getFullName()
            + "\nUsername: " + session.getCurrentUser().getUsername()
            + "\nRole: " + role.name()
            + "\nAccess Level: " + (isAdmin ? "Admin (Full Inventory & Approval Access)" : "Staff (View, Search, Sales, Request)");

        if (role == UserRole.CHAIRPERSON) {
            Object[] options = {"Create New Account", "Close"};
            int choice = JOptionPane.showOptionDialog(this, info, "Account & System Info",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);
            if (choice == 0) {
                createUserAccount();
            }
        } else {
            JOptionPane.showMessageDialog(this, info, "Account & System Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void createUserAccount() {
        JTextField usernameField = new JTextField();
        JTextField fullNameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<UserRole> roleBox = new JComboBox<>(UserRole.values());
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Full Name:"));
        form.add(fullNameField);
        form.add(new JLabel("Password (min 8 chars):"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);

        int result = JOptionPane.showConfirmDialog(this, form, "Create User Account",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            new AuthService(session).createUser(usernameField.getText(), fullNameField.getText(),
                new String(passwordField.getPassword()), (UserRole) roleBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Account created successfully!", "User Management", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Settings", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPromosData() {
        if (session == null || !session.isLoggedIn()) {
            return;
        }
        try {
            loadedBundles = bundleService.getAllBundles();
            javax.swing.JButton[] bundleButtons = { btnBundle1, btnBundle2, btnBundle3 };
            for (int i = 0; i < bundleButtons.length; i++) {
                if (i < loadedBundles.size()) {
                    Bundle b = loadedBundles.get(i);
                    bundleButtons[i].setText(String.format("%s (₱%,.2f)", b.getBundleName(), b.getPriceCentavos() / 100.0));
                    bundleButtons[i].setEnabled(true);
                } else {
                    bundleButtons[i].setText("Empty Slot");
                    bundleButtons[i].setEnabled(false);
                }
            }

            List<PreOrder> orders = preOrderService.getAllOrders();
            JLabel[] idLabels = { lbOrderI01, lbOrderI02, lbOrderI03 };
            JLabel[] nameLabels = { lblCustomerName1, lblCustomerName2, lblCustomerName3 };
            JLabel[] bundleLabels = { lblBundleNum1, lblBundleNum2, lblBundleNum3 };
            JLabel[] qtyLabels = { lblQuantity1, lblQuantity2, lblQuantity3 };
            JLabel[] totalLabels = { lblTotal1, lblTotal2, lblTotal3 };
            JLabel[] statusLabels = { lblStatus1, lblStatus2, lblStatus3 };

            for (int i = 0; i < 3; i++) {
                if (i < orders.size()) {
                    PreOrder o = orders.get(i);
                    idLabels[i].setText("PO-" + o.getOrderId());
                    nameLabels[i].setText(o.getCustomerName());
                    bundleLabels[i].setText(o.getBundleId() != null ? "Bundle #" + o.getBundleId() : "Prod #" + o.getProductId());
                    qtyLabels[i].setText(String.valueOf(o.getQuantity()));
                    totalLabels[i].setText(o.getFormattedTotal());
                    statusLabels[i].setText(o.getStatus().name());
                } else {
                    idLabels[i].setText("-");
                    nameLabels[i].setText("-");
                    bundleLabels[i].setText("-");
                    qtyLabels[i].setText("-");
                    totalLabels[i].setText("-");
                    statusLabels[i].setText("-");
                }
            }

            List<Product> products = productService.getAllProducts();
            JLabel[] codeLabels = { lblProductCode1, lblProductCode2, lblProductCode3, lblProductCode4 };
            JLabel[] descLabels = { lblProductDescription1, lblProductDescription2, lblProductDescription3, lblProductDescription4 };
            JLabel[] stockLabels = { lblProductStatus2, lblProductStatus4, lblProductStatus6, lblProductStatus8 };
            JLabel[] priceLabels = { lblProductStatus3, lblProductStatus5, lblProductStatus7, lblProductStatus9 };

            for (int i = 0; i < 4; i++) {
                if (i < products.size()) {
                    Product p = products.get(i);
                    codeLabels[i].setText("#" + p.getProductId());
                    descLabels[i].setText(p.getProductName());
                    stockLabels[i].setText("Qty: " + p.getQuantity());
                    priceLabels[i].setText(String.format("₱%,.2f", p.getPriceCentavos() / 100.0));
                } else {
                    codeLabels[i].setText("-");
                    descLabels[i].setText("-");
                    stockLabels[i].setText("-");
                    priceLabels[i].setText("-");
                }
            }
        } catch (SecurityException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Promos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBundleDetails(int index) {
        if (index < 0 || index >= loadedBundles.size()) {
            return;
        }
        Bundle b = loadedBundles.get(index);
        StringBuilder details = new StringBuilder();
        details.append("Bundle: ").append(b.getBundleName()).append("\n");
        details.append(String.format("Price: ₱%,.2f\n\nItems:\n", b.getPriceCentavos() / 100.0));
        b.getItems().forEach(item ->
            details.append(" - ").append(item.getProductName()).append(" x").append(item.getQuantity()).append("\n")
        );
        JOptionPane.showMessageDialog(this, details.toString(), "Bundle Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCreatePreOrder() {
        if (session == null || !session.isLoggedIn() || preOrderService == null) {
            JOptionPane.showMessageDialog(this, "No active user session.", "Promos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (loadedBundles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active bundles available to pre-order.", "Promos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField customerField = new JTextField();
        JTextField contactField = new JTextField();
        JComboBox<String> bundleBox = new JComboBox<>();
        for (Bundle b : loadedBundles) {
            bundleBox.addItem(b.getBundleName() + String.format(" (₱%,.2f)", b.getPriceCentavos() / 100.0));
        }
        JTextField qtyField = new JTextField("1");
        JTextField notesField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Customer Name:"));
        form.add(customerField);
        form.add(new JLabel("Contact Number:"));
        form.add(contactField);
        form.add(new JLabel("Bundle:"));
        form.add(bundleBox);
        form.add(new JLabel("Quantity:"));
        form.add(qtyField);
        form.add(new JLabel("Notes:"));
        form.add(notesField);

        int result = JOptionPane.showConfirmDialog(
            this, form, "New Pre-Order", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Bundle selected = loadedBundles.get(bundleBox.getSelectedIndex());
            int qty = Integer.parseInt(qtyField.getText().trim());
            long totalCentavos = selected.getPriceCentavos() * qty;

            int orderId = preOrderService.createPreOrder(
                customerField.getText(),
                contactField.getText(),
                selected.getBundleId(),
                null,
                qty,
                totalCentavos,
                notesField.getText()
            );
            JOptionPane.showMessageDialog(this, "Pre-Order PO-" + orderId + " created!");
            loadPromosData();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid integer.", "Promos", JOptionPane.ERROR_MESSAGE);
        } catch (SecurityException | IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Promos", JOptionPane.ERROR_MESSAGE);
        }
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
        jPanel2 = new javax.swing.JPanel();
        btnDashboard = new javax.swing.JButton();
        btnMerchInventory = new javax.swing.JButton();
        btnPromos = new javax.swing.JButton();
        btnRentalService = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblPromos = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnAddBundle = new javax.swing.JButton();
        btnAddBundleSymbol = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnBundle2 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        btnBundle1 = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        btnBundle3 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        pnlPreOrder = new javax.swing.JLabel();
        lbOrderId = new javax.swing.JLabel();
        lblCustomerName = new javax.swing.JLabel();
        lblBundleNum = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lbOrderI01 = new javax.swing.JLabel();
        lblCustomerName1 = new javax.swing.JLabel();
        lblBundleNum1 = new javax.swing.JLabel();
        lblQuantity1 = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        lblStatus1 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lbOrderI02 = new javax.swing.JLabel();
        lblCustomerName2 = new javax.swing.JLabel();
        lblBundleNum2 = new javax.swing.JLabel();
        lblQuantity2 = new javax.swing.JLabel();
        lblTotal2 = new javax.swing.JLabel();
        lblStatus2 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        lbOrderI03 = new javax.swing.JLabel();
        lblCustomerName3 = new javax.swing.JLabel();
        lblBundleNum3 = new javax.swing.JLabel();
        lblQuantity3 = new javax.swing.JLabel();
        lblTotal3 = new javax.swing.JLabel();
        lblStatus3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        btnAddBundle1 = new javax.swing.JButton();
        lblProductCode = new javax.swing.JLabel();
        lblProductDescription = new javax.swing.JLabel();
        lblProductStatus = new javax.swing.JLabel();
        lblProductStatus1 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        lblProductCode1 = new javax.swing.JLabel();
        lblProductDescription1 = new javax.swing.JLabel();
        lblProductStatus2 = new javax.swing.JLabel();
        lblProductStatus3 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        lblProductCode2 = new javax.swing.JLabel();
        lblProductDescription2 = new javax.swing.JLabel();
        lblProductStatus4 = new javax.swing.JLabel();
        lblProductStatus5 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        lblProductCode3 = new javax.swing.JLabel();
        lblProductDescription3 = new javax.swing.JLabel();
        lblProductStatus6 = new javax.swing.JLabel();
        lblProductStatus7 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        lblProductCode4 = new javax.swing.JLabel();
        lblProductDescription4 = new javax.swing.JLabel();
        lblProductStatus8 = new javax.swing.JLabel();
        lblProductStatus9 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1280, 800));

        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));

        jPanel2.setBackground(new java.awt.Color(30, 30, 30));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(null, new java.awt.Color(192, 192, 192)));
        jPanel2.setPreferredSize(new java.awt.Dimension(245, 800));

        btnDashboard.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnDashboard.setForeground(new java.awt.Color(245, 245, 245));
        btnDashboard.setText("DASHBOARD");
        btnDashboard.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnDashboard.setContentAreaFilled(false);
        btnDashboard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnDashboard.setMaximumSize(new java.awt.Dimension(90, 20));
        btnDashboard.setMinimumSize(new java.awt.Dimension(90, 20));
        btnDashboard.setPreferredSize(new java.awt.Dimension(90, 20));

        btnMerchInventory.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnMerchInventory.setForeground(new java.awt.Color(245, 245, 245));
        btnMerchInventory.setText("MERCH INVENTORY");
        btnMerchInventory.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnMerchInventory.setContentAreaFilled(false);
        btnMerchInventory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMerchInventory.setMaximumSize(new java.awt.Dimension(90, 20));
        btnMerchInventory.setMinimumSize(new java.awt.Dimension(90, 20));
        btnMerchInventory.setPreferredSize(new java.awt.Dimension(90, 20));

        btnPromos.setBackground(new java.awt.Color(11, 52, 90));
        btnPromos.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnPromos.setForeground(new java.awt.Color(192, 210, 224));
        btnPromos.setText("PROMOS");
        btnPromos.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.setMaximumSize(new java.awt.Dimension(90, 20));
        btnPromos.setMinimumSize(new java.awt.Dimension(90, 20));
        btnPromos.setPreferredSize(new java.awt.Dimension(90, 20));

        btnRentalService.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnRentalService.setForeground(new java.awt.Color(245, 245, 245));
        btnRentalService.setText("RENTAL SERVICE");
        btnRentalService.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        btnRentalService.setContentAreaFilled(false);
        btnRentalService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRentalService.setMaximumSize(new java.awt.Dimension(90, 20));
        btnRentalService.setMinimumSize(new java.awt.Dimension(90, 20));
        btnRentalService.setPreferredSize(new java.awt.Dimension(90, 20));

        jButton5.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(245, 245, 245));
        jButton5.setText("Log Out");
        jButton5.setContentAreaFilled(false);
        jButton5.setMaximumSize(new java.awt.Dimension(90, 20));
        jButton5.setMinimumSize(new java.awt.Dimension(90, 20));
        jButton5.setPreferredSize(new java.awt.Dimension(90, 20));
        jButton5.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(245, 245, 245));
        jButton6.setText("Settings");
        jButton6.setContentAreaFilled(false);
        jButton6.setMaximumSize(new java.awt.Dimension(90, 20));
        jButton6.setMinimumSize(new java.awt.Dimension(90, 20));
        jButton6.setPreferredSize(new java.awt.Dimension(90, 20));
        jButton6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRentalService, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDashboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMerchInventory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46))
                    .addComponent(btnPromos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnMerchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRentalService, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        jPanel3.setBackground(new java.awt.Color(9, 30, 57));
        jPanel3.setPreferredSize(new java.awt.Dimension(1035, 130));

        lblPromos.setFont(new java.awt.Font("Agency FB", 1, 50)); // NOI18N
        lblPromos.setForeground(new java.awt.Color(245, 245, 245));
        lblPromos.setText("PROMOS");
        lblPromos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(245, 245, 245));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Technicals");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(245, 245, 245));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Elijah Yanto");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(245, 245, 245));
        jLabel4.setText("Create merchandise bundles, manage promotional offers, and monitor customer pre-orders.");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        jPanel4.setBackground(new java.awt.Color(191, 205, 221));

        btnAddBundle.setBackground(new java.awt.Color(218, 245, 228));
        btnAddBundle.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnAddBundle.setForeground(new java.awt.Color(33, 86, 124));
        btnAddBundle.setText("ADD BUNDLE");
        btnAddBundle.setContentAreaFilled(false);
        btnAddBundle.addActionListener(this::btnAddBundleActionPerformed);

        btnAddBundleSymbol.setFont(new java.awt.Font("Segoe UI Historic", 1, 52)); // NOI18N
        btnAddBundleSymbol.setForeground(new java.awt.Color(33, 86, 124));
        btnAddBundleSymbol.setText("+");
        btnAddBundleSymbol.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAddBundleSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(btnAddBundle)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(btnAddBundleSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddBundle)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(227, 232, 238));

        btnBundle2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnBundle2.setForeground(new java.awt.Color(33, 86, 124));
        btnBundle2.setText("BUNDLE 2");
        btnBundle2.setBorder(null);
        btnBundle2.setContentAreaFilled(false);
        btnBundle2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBundle2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnBundle2.addActionListener(this::btnBundle2ActionPerformed);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBundle2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(btnBundle2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnBundle1.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnBundle1.setForeground(new java.awt.Color(33, 86, 124));
        btnBundle1.setText("BUNDLE 1");
        btnBundle1.setBorder(null);
        btnBundle1.setContentAreaFilled(false);
        btnBundle1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBundle1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBundle1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(btnBundle1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnBundle3.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnBundle3.setForeground(new java.awt.Color(33, 86, 124));
        btnBundle3.setText("BUNDLE 3");
        btnBundle3.setBorder(null);
        btnBundle3.setContentAreaFilled(false);
        btnBundle3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnBundle3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBundle3, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(125, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addComponent(btnBundle3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI Historic", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 86, 124));
        jLabel3.setText("BUNDLE SELECTION");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel6.setBackground(new java.awt.Color(227, 232, 238));

        jPanel11.setBackground(new java.awt.Color(9, 30, 57));

        pnlPreOrder.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        pnlPreOrder.setForeground(new java.awt.Color(245, 245, 245));
        pnlPreOrder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlPreOrder.setText("PRE-ORDER LISTINGS");
        pnlPreOrder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(146, 146, 146)
                    .addComponent(pnlPreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(161, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 42, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlPreOrder, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        lbOrderId.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId.setText("Order ID");

        lblCustomerName.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblCustomerName.setForeground(new java.awt.Color(33, 86, 124));
        lblCustomerName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCustomerName.setText("Customer Name");

        lblBundleNum.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblBundleNum.setForeground(new java.awt.Color(33, 86, 124));
        lblBundleNum.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBundleNum.setText("Bundle");

        lblQuantity.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblQuantity.setForeground(new java.awt.Color(33, 86, 124));
        lblQuantity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuantity.setText("Quantity");

        lblTotal.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(33, 86, 124));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal.setText("Total");

        lblStatus.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(33, 86, 124));
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("Status");

        lbOrderI01.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderI01.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderI01.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderI01.setText("JK-001");

        lblCustomerName1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblCustomerName1.setForeground(new java.awt.Color(33, 86, 124));
        lblCustomerName1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCustomerName1.setText("Frich Ramos");

        lblBundleNum1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblBundleNum1.setForeground(new java.awt.Color(33, 86, 124));
        lblBundleNum1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBundleNum1.setText("1");

        lblQuantity1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblQuantity1.setForeground(new java.awt.Color(33, 86, 124));
        lblQuantity1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuantity1.setText("2");

        lblTotal1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblTotal1.setForeground(new java.awt.Color(33, 86, 124));
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal1.setText("300.00");

        lblStatus1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblStatus1.setForeground(new java.awt.Color(33, 86, 124));
        lblStatus1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus1.setText("Pending");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOrderI01, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCustomerName1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBundleNum1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQuantity1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderI01, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerName1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBundleNum1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantity1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lbOrderI02.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderI02.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderI02.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderI02.setText("JK-002");

        lblCustomerName2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblCustomerName2.setForeground(new java.awt.Color(33, 86, 124));
        lblCustomerName2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCustomerName2.setText("Kyla Tychingco");

        lblBundleNum2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblBundleNum2.setForeground(new java.awt.Color(33, 86, 124));
        lblBundleNum2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBundleNum2.setText("2");

        lblQuantity2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblQuantity2.setForeground(new java.awt.Color(33, 86, 124));
        lblQuantity2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuantity2.setText("1");

        lblTotal2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblTotal2.setForeground(new java.awt.Color(33, 86, 124));
        lblTotal2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal2.setText("150.00");

        lblStatus2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblStatus2.setForeground(new java.awt.Color(33, 86, 124));
        lblStatus2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus2.setText("Pending");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOrderI02, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCustomerName2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBundleNum2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQuantity2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderI02, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerName2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBundleNum2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantity2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lbOrderI03.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderI03.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderI03.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderI03.setText("JK-003");

        lblCustomerName3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblCustomerName3.setForeground(new java.awt.Color(33, 86, 124));
        lblCustomerName3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCustomerName3.setText("Elijah Yanto");

        lblBundleNum3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblBundleNum3.setForeground(new java.awt.Color(33, 86, 124));
        lblBundleNum3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBundleNum3.setText("3");

        lblQuantity3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblQuantity3.setForeground(new java.awt.Color(33, 86, 124));
        lblQuantity3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblQuantity3.setText("3");

        lblTotal3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblTotal3.setForeground(new java.awt.Color(33, 86, 124));
        lblTotal3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotal3.setText("450.00");

        lblStatus3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblStatus3.setForeground(new java.awt.Color(33, 86, 124));
        lblStatus3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus3.setText("Pending");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOrderI03, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCustomerName3, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblBundleNum3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQuantity3, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTotal3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderI03, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerName3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBundleNum3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantity3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(lbOrderId, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBundleNum, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderId, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBundleNum, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(227, 232, 238));

        jPanel15.setBackground(new java.awt.Color(191, 205, 221));

        btnAddBundle1.setBackground(new java.awt.Color(218, 245, 228));
        btnAddBundle1.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnAddBundle1.setForeground(new java.awt.Color(33, 86, 124));
        btnAddBundle1.setText("STOCK INVENTORY");
        btnAddBundle1.setContentAreaFilled(false);
        btnAddBundle1.addActionListener(this::btnAddBundle1ActionPerformed);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(btnAddBundle1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnAddBundle1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblProductCode.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductCode.setForeground(new java.awt.Color(33, 86, 124));
        lblProductCode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductCode.setText("Code");

        lblProductDescription.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductDescription.setForeground(new java.awt.Color(33, 86, 124));
        lblProductDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductDescription.setText("Description");

        lblProductStatus.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus.setText("Status");

        lblProductStatus1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus1.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus1.setText("Quantity");

        lblProductCode1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductCode1.setForeground(new java.awt.Color(33, 86, 124));
        lblProductCode1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductCode1.setText("K1");

        lblProductDescription1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductDescription1.setForeground(new java.awt.Color(33, 86, 124));
        lblProductDescription1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductDescription1.setText("Keychain");

        lblProductStatus2.setBackground(new java.awt.Color(254, 225, 224));
        lblProductStatus2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus2.setForeground(new java.awt.Color(224, 46, 42));
        lblProductStatus2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus2.setText("Low");
        lblProductStatus2.setOpaque(true);

        lblProductStatus3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus3.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus3.setText("7");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblProductCode1)
                .addGap(18, 18, 18)
                .addComponent(lblProductDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProductStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblProductStatus3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductDescription1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblProductCode2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductCode2.setForeground(new java.awt.Color(33, 86, 124));
        lblProductCode2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductCode2.setText("L1");

        lblProductDescription2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductDescription2.setForeground(new java.awt.Color(33, 86, 124));
        lblProductDescription2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductDescription2.setText("Lanyards");

        lblProductStatus4.setBackground(new java.awt.Color(254, 225, 224));
        lblProductStatus4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus4.setForeground(new java.awt.Color(224, 46, 42));
        lblProductStatus4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus4.setText("Low");
        lblProductStatus4.setOpaque(true);

        lblProductStatus5.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus5.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus5.setText("2");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblProductCode2)
                .addGap(18, 18, 18)
                .addComponent(lblProductDescription2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProductStatus4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblProductStatus5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductDescription2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus5, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblProductCode3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductCode3.setForeground(new java.awt.Color(33, 86, 124));
        lblProductCode3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductCode3.setText("BP1");

        lblProductDescription3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductDescription3.setForeground(new java.awt.Color(33, 86, 124));
        lblProductDescription3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductDescription3.setText("Button Pins");

        lblProductStatus6.setBackground(new java.awt.Color(254, 243, 199));
        lblProductStatus6.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus6.setForeground(new java.awt.Color(180, 83, 9));
        lblProductStatus6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus6.setText("Mid");
        lblProductStatus6.setOpaque(true);

        lblProductStatus7.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus7.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus7.setText("7");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblProductCode3)
                .addGap(18, 18, 18)
                .addComponent(lblProductDescription3, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProductStatus6, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblProductStatus7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductDescription3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblProductCode4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductCode4.setForeground(new java.awt.Color(33, 86, 124));
        lblProductCode4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductCode4.setText("S1");

        lblProductDescription4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductDescription4.setForeground(new java.awt.Color(33, 86, 124));
        lblProductDescription4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductDescription4.setText("Shirts");

        lblProductStatus8.setBackground(new java.awt.Color(218, 245, 228));
        lblProductStatus8.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus8.setForeground(new java.awt.Color(21, 128, 61));
        lblProductStatus8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus8.setText("High");
        lblProductStatus8.setOpaque(true);

        lblProductStatus9.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lblProductStatus9.setForeground(new java.awt.Color(33, 86, 124));
        lblProductStatus9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductStatus9.setText("7");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblProductCode4)
                .addGap(18, 18, 18)
                .addComponent(lblProductDescription4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblProductStatus8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblProductStatus9, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductDescription4, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(lblProductCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblProductDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblProductStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblProductStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblProductStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );

        jPanel16.setBackground(new java.awt.Color(191, 205, 221));

        jButton1.setBackground(new java.awt.Color(191, 205, 221));
        jButton1.setFont(new java.awt.Font("Segoe UI Historic", 1, 24)); // NOI18N
        jButton1.setForeground(new java.awt.Color(33, 86, 124));
        jButton1.setText("ADD NEW ORDER");
        jButton1.setActionCommand("ADD NEW ORDER");
        jButton1.setBorder(null);
        jButton1.setContentAreaFilled(false);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 487, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void btnAddBundleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBundleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddBundleActionPerformed

    private void btnBundle2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBundle2ActionPerformed
        showBundleDetails(1);
    }//GEN-LAST:event_btnBundle2ActionPerformed

    private void btnAddBundle1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBundle1ActionPerformed
        handleCreatePreOrder();
    }//GEN-LAST:event_btnAddBundle1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new PromosPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBundle;
    private javax.swing.JButton btnAddBundle1;
    private javax.swing.JButton btnAddBundleSymbol;
    private javax.swing.JButton btnBundle1;
    private javax.swing.JButton btnBundle2;
    private javax.swing.JButton btnBundle3;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnMerchInventory;
    private javax.swing.JButton btnPromos;
    private javax.swing.JButton btnRentalService;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JLabel lbOrderI01;
    private javax.swing.JLabel lbOrderI02;
    private javax.swing.JLabel lbOrderI03;
    private javax.swing.JLabel lbOrderId;
    private javax.swing.JLabel lblBundleNum;
    private javax.swing.JLabel lblBundleNum1;
    private javax.swing.JLabel lblBundleNum2;
    private javax.swing.JLabel lblBundleNum3;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblCustomerName1;
    private javax.swing.JLabel lblCustomerName2;
    private javax.swing.JLabel lblCustomerName3;
    private javax.swing.JLabel lblProductCode;
    private javax.swing.JLabel lblProductCode1;
    private javax.swing.JLabel lblProductCode2;
    private javax.swing.JLabel lblProductCode3;
    private javax.swing.JLabel lblProductCode4;
    private javax.swing.JLabel lblProductDescription;
    private javax.swing.JLabel lblProductDescription1;
    private javax.swing.JLabel lblProductDescription2;
    private javax.swing.JLabel lblProductDescription3;
    private javax.swing.JLabel lblProductDescription4;
    private javax.swing.JLabel lblProductStatus;
    private javax.swing.JLabel lblProductStatus1;
    private javax.swing.JLabel lblProductStatus2;
    private javax.swing.JLabel lblProductStatus3;
    private javax.swing.JLabel lblProductStatus4;
    private javax.swing.JLabel lblProductStatus5;
    private javax.swing.JLabel lblProductStatus6;
    private javax.swing.JLabel lblProductStatus7;
    private javax.swing.JLabel lblProductStatus8;
    private javax.swing.JLabel lblProductStatus9;
    private javax.swing.JLabel lblPromos;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblQuantity1;
    private javax.swing.JLabel lblQuantity2;
    private javax.swing.JLabel lblQuantity3;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatus1;
    private javax.swing.JLabel lblStatus2;
    private javax.swing.JLabel lblStatus3;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal2;
    private javax.swing.JLabel lblTotal3;
    private javax.swing.JLabel pnlPreOrder;
    // End of variables declaration//GEN-END:variables
}
