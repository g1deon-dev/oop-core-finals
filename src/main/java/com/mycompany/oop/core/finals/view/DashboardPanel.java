package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.StockMovement;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.AuthorizationService;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.service.InventoryService;
import com.mycompany.oop.core.finals.service.ProductService;
import com.mycompany.oop.core.finals.service.SalesService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 *
 * @author Frich Ramos
 */
public class DashboardPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardPanel.class.getName());

    private UserSession session;
    private AuthorizationService authService;
    private ProductService productService;
    private InventoryService inventoryService;
    private SalesService salesService;

    private final JButton btnPromosNav = new JButton("PROMOS");

    /**
     * Creates new form DashboardPanel
     */
    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(UserSession session) {
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
        this.productService = new ProductService(authService);
        this.inventoryService = new InventoryService(authService, session);
        this.salesService = new SalesService(authService, session);

        String fullName = session.getCurrentUser().getFullName();
        jLabel2.setText("Welcome back, " + fullName + "!");
        jLabel5.setText(fullName);
        jLabel4.setText(session.getCurrentUser().getRole().name());

        loadDashboardData();
    }

    private void polishLayout() {
        setTitle("CORE — Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Color sidebarBg = new Color(30, 30, 30);
        Color headerNavy = new Color(9, 30, 57);
        Color activeNavBg = new Color(11, 52, 90);
        Color accentIce = new Color(192, 210, 224);
        Color textLight = new Color(245, 245, 245);
        Color cardBg = new Color(227, 232, 238);
        Color subTitleBlue = new Color(33, 86, 124);

        // ── Sidebar (matched to MerchInventoryPanel & PromosPanel) ───────────
        jPanel2.setBackground(sidebarBg);
        jPanel2.setBorder(BorderFactory.createEtchedBorder(null, Color.lightGray));
        jPanel2.removeAll();
        jPanel2.setLayout(new BorderLayout());

        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(24, 16, 0, 16));

        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        lblLogo.setPreferredSize(new Dimension(210, 86));
        URL logoUrl = getClass().getResource("/com/mycompany/oop/core/finals/view/logo.png");
        if (logoUrl != null) {
            lblLogo.setIcon(new ImageIcon(logoUrl));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            lblLogo.setText("CORE");
            lblLogo.setFont(new Font("Agency FB", Font.BOLD, 36));
            lblLogo.setForeground(accentIce);
        }
        navPanel.add(lblLogo);
        navPanel.add(Box.createVerticalStrut(48));

        // Active: DASHBOARD
        jbtnDashBoard.setText("DASHBOARD");
        styleSidebarButton(jbtnDashBoard, true, activeNavBg, accentIce, textLight);
        navPanel.add(jbtnDashBoard);
        navPanel.add(Box.createVerticalStrut(6));

        // Inactive nav items
        jButton3.setText("MERCH INVENTORY");
        styleSidebarButton(jButton3, false, activeNavBg, accentIce, textLight);
        navPanel.add(jButton3);
        navPanel.add(Box.createVerticalStrut(6));

        styleSidebarButton(btnPromosNav, false, activeNavBg, accentIce, textLight);
        navPanel.add(btnPromosNav);
        navPanel.add(Box.createVerticalStrut(6));

        jButton7.setText("RENTAL SERVICE");
        styleSidebarButton(jButton7, false, activeNavBg, accentIce, textLight);
        navPanel.add(jButton7);

        jPanel2.add(navPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 32, 16));

        jButton6.setText("Log Out");
        styleSidebarButton(jButton6, false, activeNavBg, accentIce, accentIce);
        jButton6.setFont(new Font("Segoe UI Historic", Font.PLAIN, 12));
        bottomPanel.add(jButton6);

        jPanel2.add(bottomPanel, BorderLayout.SOUTH);

        // ── Header (matched to MerchInventoryPanel) ──────────────────────────
        jPanel3.setBackground(headerNavy);
        jLabel2.setFont(new Font("Agency FB", Font.BOLD, 44));
        jLabel2.setForeground(textLight);
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 22, 650, 52));

        jLabel3.setFont(new Font("Segoe UI Historic", Font.PLAIN, 14));
        jLabel3.setForeground(textLight);
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(24, 82, 500, 24));

        jLabel5.setFont(new Font("Segoe UI Historic", Font.BOLD, 12));
        jLabel5.setForeground(textLight);
        jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 56, 150, 20));

        jLabel4.setFont(new Font("Segoe UI Historic", Font.PLAIN, 12));
        jLabel4.setForeground(accentIce);
        jLabel4.setHorizontalAlignment(SwingConstants.CENTER);
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 76, 150, 20));

        // ── Main Content & Cards (matched to MerchInventoryPanel) ────────────
        Color cardMedium = new Color(191, 205, 221);
        Color darkText = new Color(30, 30, 30);

        jPanel1.setBackground(textLight);
        jPanel6.setBackground(cardBg);
        jPanel7.setBackground(cardBg);
        jPanel8.setBackground(cardBg);

        jPanel14.setBackground(cardMedium);
        jPanel15.setBackground(textLight);
        jPanel9.setBackground(textLight);
        jPanel10.setBackground(textLight);
        jPanel11.setBackground(textLight);
        jPanel12.setBackground(textLight);
        jPanel13.setBackground(textLight);

        // Card section headers
        jLabel14.setFont(new Font("Segoe UI Historic", Font.BOLD, 18));
        jLabel14.setForeground(headerNavy);
        jLabel9.setFont(new Font("Segoe UI Historic", Font.BOLD, 14));
        jLabel9.setForeground(subTitleBlue);
        jLabel8.setFont(new Font("Segoe UI Historic", Font.BOLD, 18));
        jLabel8.setForeground(headerNavy);

        JLabel[] dayHeaders = { jLabel7, jLabel11, jLabel12, jLabel13, jLabel54, jLabel55, jLabel56 };
        for (JLabel dh : dayHeaders) {
            dh.setFont(new Font("Segoe UI Historic", Font.BOLD, 13));
            dh.setForeground(subTitleBlue);
        }

        // Calendar day numbers on light card
        JLabel[] calDays = {
            jLabel26, jLabel66, jLabel67, jLabel68, jLabel69, jLabel70, jLabel71,
            jLabel72, jLabel73, jLabel74, jLabel75, jLabel76, jLabel77, jLabel78,
            jLabel80, jLabel81, jLabel82, jLabel83, jLabel85, jLabel86, jLabel87,
            jLabel88, jLabel90, jLabel91, jLabel92, jLabel93, jLabel95, jLabel96,
            jLabel97, jLabel98
        };
        for (JLabel cd : calDays) {
            cd.setForeground(subTitleBlue);
        }

        // Total Sales card typography (matched to pnlTotalStock)
        jLabel27.setFont(new Font("Segoe UI Historic", Font.BOLD, 16));
        jLabel27.setForeground(headerNavy);
        jLabel21.setText("PHP");
        jLabel21.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jLabel21.setForeground(subTitleBlue);
        jPanel14.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 85, 40, 30));
        jLabel23.setFont(new Font("Segoe UI", Font.BOLD, 42));
        jLabel23.setForeground(subTitleBlue);
        jLabel24.setFont(new Font("Segoe UI Historic", Font.BOLD, 13));
        jLabel24.setForeground(headerNavy);
        jLabel22.setFont(new Font("Segoe UI Historic", Font.PLAIN, 13));
        jLabel22.setForeground(headerNavy);
        jLabel20.setFont(new Font("Segoe UI Historic", Font.PLAIN, 13));
        jLabel20.setForeground(headerNavy);

        // Stock Status card typography
        jLabel28.setFont(new Font("Segoe UI Historic", Font.BOLD, 16));
        jLabel28.setForeground(headerNavy);
        JLabel[] stockLabels = { jLabel29, jLabel30, jLabel31, jLabel32, jLabel25 };
        for (JLabel sl : stockLabels) {
            sl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            sl.setForeground(darkText);
        }

        // Recent Activities labels
        jPanel10.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 10, 435, 20));
        jPanel11.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 10, 435, 20));
        jPanel12.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 10, 435, 20));
        jPanel13.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 10, 435, 20));
        JLabel[] actLabels = { jLabel16, jLabel17, jLabel18, jLabel15 };
        for (JLabel al : actLabels) {
            al.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            al.setForeground(subTitleBlue);
        }

        // Wrap jPanel1 in a JScrollPane so it fits smaller laptop screens
        JScrollPane scroll = new JScrollPane(jPanel1);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scroll, BorderLayout.CENTER);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1280, screen.width - 40);
        int height = Math.min(800, screen.height - 60);
        setSize(width, height);
        setLocationRelativeTo(null);
    }

    private void styleSidebarButton(JButton btn, boolean active, Color activeBg, Color accentIce, Color fg) {
        btn.setFont(new Font("Segoe UI Historic", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setPreferredSize(new Dimension(210, 36));
        if (active) {
            btn.setBackground(activeBg);
            btn.setOpaque(true);
            btn.setContentAreaFilled(true);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accentIce),
                BorderFactory.createEmptyBorder(0, 9, 0, 12)
            ));
        } else {
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        }
    }

    private void setupListeners() {
        jbtnDashBoard.addActionListener(e -> loadDashboardData());
        jButton3.addActionListener(e -> {
            new MerchInventoryPanel(session).setVisible(true);
            this.dispose();
        });
        btnPromosNav.addActionListener(e -> {
            new PromosPanel(session).setVisible(true);
            this.dispose();
        });
        jButton7.addActionListener(e -> JOptionPane.showMessageDialog(
            this,
            "Rental Service is managed by the Rental team (coming soon).",
            "Rental Service",
            JOptionPane.INFORMATION_MESSAGE
        ));
    }

    private void loadDashboardData() {
        if (session == null || !session.isLoggedIn()) {
            return;
        }
        try {
            // 1. Total Sales
            List<Sale> sales = salesService.getAllSales();
            long totalCentavos = 0;
            for (Sale s : sales) {
                totalCentavos += s.getTotalCentavos();
            }
            jLabel23.setText(String.format("%,.2f", totalCentavos / 100.0));
            jLabel20.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            jLabel24.setText(sales.size() + " sales");
            jLabel22.setText("CORE Inventory");

            // 2. Stock Status (top 5 active products)
            List<Product> products = productService.getAllProducts();
            JLabel[] stockLabels = { jLabel29, jLabel30, jLabel31, jLabel32, jLabel25 };
            for (int i = 0; i < stockLabels.length; i++) {
                if (i < products.size()) {
                    Product p = products.get(i);
                    String status = p.getQuantity() <= 0 ? "OUT"
                        : (p.getQuantity() <= p.getLowStockThreshold() ? "LOW" : "OK");
                    stockLabels[i].setText(String.format("%-26s  %d left (%s)", p.getProductName(), p.getQuantity(), status));
                } else {
                    stockLabels[i].setText("—");
                }
            }

            // 3. Recent Activities (latest 4 stock movements)
            List<StockMovement> movements = inventoryService.getAllMovements();
            JLabel[] activityLabels = { jLabel16, jLabel17, jLabel18, jLabel15 };
            for (int i = 0; i < activityLabels.length; i++) {
                if (i < movements.size()) {
                    StockMovement m = movements.get(i);
                    String sign = m.getChangeAmount() > 0 ? "+" : "";
                    activityLabels[i].setText(String.format(
                        "Product #%d  %s%d  (%s)",
                        m.getProductId(), sign, m.getChangeAmount(), m.getReason()
                    ));
                } else {
                    activityLabels[i].setText("No recent activity");
                }
            }
        } catch (SecurityException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard", JOptionPane.ERROR_MESSAGE);
        }
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
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jbtnDashBoard = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));
        jPanel1.setName("⤷ Promo"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(30, 30, 30));
        jPanel2.setPreferredSize(new java.awt.Dimension(245, 800));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(245, 245, 245));
        jLabel1.setText("ꜜ");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setPreferredSize(new java.awt.Dimension(20, 20));
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 260, -1, -1));

        jButton3.setBackground(new java.awt.Color(30, 30, 30));
        jButton3.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(245, 245, 245));
        jButton3.setText("Merch Inventory");
        jButton3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton3.setPreferredSize(new java.awt.Dimension(205, 45));
        jPanel2.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        jButton5.setBackground(new java.awt.Color(30, 30, 30));
        jButton5.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(245, 245, 245));
        jButton5.setText("Settings");
        jButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton5.setPreferredSize(new java.awt.Dimension(205, 45));
        jPanel2.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, -1, -1));

        jPanel4.setBackground(new java.awt.Color(30, 30, 30));
        jPanel4.setForeground(new java.awt.Color(245, 245, 245));
        jPanel4.setToolTipText("");
        jPanel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(205, 45));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(245, 245, 245));
        jLabel6.setText("⤷ Promo");
        jLabel6.setToolTipText("");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 99, 20));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, -1, -1));

        jButton6.setBackground(new java.awt.Color(30, 30, 30));
        jButton6.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(245, 245, 245));
        jButton6.setText("Log Out");
        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton6.setPreferredSize(new java.awt.Dimension(205, 45));
        jButton6.addActionListener(this::jButton6ActionPerformed);
        jPanel2.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 660, -1, -1));

        jButton7.setBackground(new java.awt.Color(30, 30, 30));
        jButton7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(245, 245, 245));
        jButton7.setText("Rental Service");
        jButton7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton7.setPreferredSize(new java.awt.Dimension(205, 45));
        jPanel2.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, -1, -1));

        jPanel5.setBackground(new java.awt.Color(163, 189, 211));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jbtnDashBoard.setBackground(new java.awt.Color(163, 189, 211));
        jbtnDashBoard.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jbtnDashBoard.setForeground(new java.awt.Color(30, 30, 30));
        jbtnDashBoard.setText("Dashboard");
        jbtnDashBoard.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jbtnDashBoard.setPreferredSize(new java.awt.Dimension(205, 45));
        jPanel5.add(jbtnDashBoard, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 250, 70));

        jLabel10.setBackground(new java.awt.Color(13, 47, 90));
        jLabel10.setFont(new java.awt.Font("Bodoni MT Black", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 204, 255));
        jLabel10.setText("CORE");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jPanel3.setBackground(new java.awt.Color(13, 47, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(1035, 135));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(245, 245, 245));
        jLabel2.setText("Welcome back, Elijah Yanto!");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 490, 50));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(245, 245, 245));
        jLabel3.setText("Let's take a detailed look at your inventory today.");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(245, 245, 245));
        jLabel4.setText("Organization Member");
        jPanel3.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 80, -1, -1));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(245, 245, 245));
        jLabel5.setText("Elijah Yanto");
        jPanel3.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 60, -1, -1));

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 0, 1180, 140));

        jPanel6.setBackground(new java.awt.Color(206, 208, 211));
        jPanel6.setForeground(java.awt.Color.white);
        jPanel6.setPreferredSize(new java.awt.Dimension(515, 300));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel7.setText("Fri ");
        jPanel6.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 60, 30, -1));

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel9.setText("November 2026");
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 110, -1));

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel11.setText("Sat ");
        jPanel6.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 60, 30, -1));

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel12.setText("Sun ");
        jPanel6.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 30, -1));

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel13.setText("Mon ");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 40, -1));

        jLabel54.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel54.setText("Tue ");
        jPanel6.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 30, -1));

        jLabel55.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel55.setText("Wed ");
        jPanel6.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 60, 40, -1));

        jLabel56.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel56.setText("Thu ");
        jPanel6.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 30, -1));

        jPanel9.setBackground(new java.awt.Color(13, 47, 90));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel26.setBackground(new java.awt.Color(13, 47, 90));
        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(204, 204, 204));
        jLabel26.setText("   29");
        jPanel9.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 40, 30));

        jLabel66.setBackground(new java.awt.Color(13, 47, 90));
        jLabel66.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(204, 204, 204));
        jLabel66.setText("   1");
        jLabel66.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel9.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 30));

        jLabel67.setBackground(new java.awt.Color(13, 47, 90));
        jLabel67.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(204, 204, 204));
        jLabel67.setText("   8");
        jPanel9.add(jLabel67, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 40, 30));

        jLabel68.setBackground(new java.awt.Color(13, 47, 90));
        jLabel68.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(204, 204, 204));
        jLabel68.setText("   15");
        jPanel9.add(jLabel68, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 40, 30));

        jLabel69.setBackground(new java.awt.Color(13, 47, 90));
        jLabel69.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(204, 204, 204));
        jLabel69.setText("   22");
        jPanel9.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 40, 30));

        jLabel70.setBackground(new java.awt.Color(13, 47, 90));
        jLabel70.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(204, 204, 204));
        jLabel70.setText("   2");
        jPanel9.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 40, 30));

        jLabel71.setBackground(new java.awt.Color(13, 47, 90));
        jLabel71.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(204, 204, 204));
        jLabel71.setText("   9");
        jPanel9.add(jLabel71, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 40, 30));

        jLabel72.setBackground(new java.awt.Color(13, 47, 90));
        jLabel72.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(204, 204, 204));
        jLabel72.setText("   16");
        jPanel9.add(jLabel72, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 40, 30));

        jLabel73.setBackground(new java.awt.Color(13, 47, 90));
        jLabel73.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(204, 204, 204));
        jLabel73.setText("   23");
        jPanel9.add(jLabel73, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 40, 30));

        jLabel74.setBackground(new java.awt.Color(13, 47, 90));
        jLabel74.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(204, 204, 204));
        jLabel74.setText("   30");
        jPanel9.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, 40, 30));

        jLabel75.setBackground(new java.awt.Color(13, 47, 90));
        jLabel75.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(204, 204, 204));
        jLabel75.setText("   3");
        jPanel9.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 40, 30));

        jLabel76.setBackground(new java.awt.Color(13, 47, 90));
        jLabel76.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(204, 204, 204));
        jLabel76.setText("   10");
        jPanel9.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 50, 40, 30));

        jLabel77.setBackground(new java.awt.Color(13, 47, 90));
        jLabel77.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel77.setForeground(new java.awt.Color(204, 204, 204));
        jLabel77.setText("   17");
        jPanel9.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 90, 40, 30));

        jLabel78.setBackground(new java.awt.Color(13, 47, 90));
        jLabel78.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(204, 204, 204));
        jLabel78.setText("   24");
        jPanel9.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 130, 40, 30));

        jLabel80.setBackground(new java.awt.Color(13, 47, 90));
        jLabel80.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(204, 204, 204));
        jLabel80.setText("   4");
        jPanel9.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, 40, 30));

        jLabel81.setBackground(new java.awt.Color(13, 47, 90));
        jLabel81.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(204, 204, 204));
        jLabel81.setText("   11");
        jPanel9.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 50, 40, 30));

        jLabel82.setBackground(new java.awt.Color(13, 47, 90));
        jLabel82.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel82.setForeground(new java.awt.Color(204, 204, 204));
        jLabel82.setText("   18");
        jPanel9.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 90, 40, 30));

        jLabel83.setBackground(new java.awt.Color(13, 47, 90));
        jLabel83.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(204, 204, 204));
        jLabel83.setText("   25");
        jPanel9.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, 40, 30));

        jLabel85.setBackground(new java.awt.Color(13, 47, 90));
        jLabel85.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(204, 204, 204));
        jLabel85.setText("   5");
        jPanel9.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 40, 30));

        jLabel86.setBackground(new java.awt.Color(13, 47, 90));
        jLabel86.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(204, 204, 204));
        jLabel86.setText("   12");
        jPanel9.add(jLabel86, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 40, 30));

        jLabel87.setBackground(new java.awt.Color(13, 47, 90));
        jLabel87.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(204, 204, 204));
        jLabel87.setText("   19");
        jPanel9.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 90, 40, 30));

        jLabel88.setBackground(new java.awt.Color(13, 47, 90));
        jLabel88.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(204, 204, 204));
        jLabel88.setText("   26");
        jPanel9.add(jLabel88, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 130, 40, 30));

        jLabel90.setBackground(new java.awt.Color(13, 47, 90));
        jLabel90.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(204, 204, 204));
        jLabel90.setText("   6");
        jPanel9.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 40, 30));

        jLabel91.setBackground(new java.awt.Color(13, 47, 90));
        jLabel91.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel91.setForeground(new java.awt.Color(204, 204, 204));
        jLabel91.setText("   13");
        jPanel9.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 50, 40, 30));

        jLabel92.setBackground(new java.awt.Color(13, 47, 90));
        jLabel92.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel92.setForeground(new java.awt.Color(204, 204, 204));
        jLabel92.setText("   20");
        jPanel9.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 40, 30));

        jLabel93.setBackground(new java.awt.Color(13, 47, 90));
        jLabel93.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel93.setForeground(new java.awt.Color(204, 204, 204));
        jLabel93.setText("   27");
        jPanel9.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 130, 40, 30));

        jLabel95.setBackground(new java.awt.Color(13, 47, 90));
        jLabel95.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel95.setForeground(new java.awt.Color(204, 204, 204));
        jLabel95.setText("   7");
        jPanel9.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, 40, 30));

        jLabel96.setBackground(new java.awt.Color(13, 47, 90));
        jLabel96.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel96.setForeground(new java.awt.Color(204, 204, 204));
        jLabel96.setText("   14");
        jPanel9.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 50, 40, 30));

        jLabel97.setBackground(new java.awt.Color(13, 47, 90));
        jLabel97.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel97.setForeground(new java.awt.Color(204, 204, 204));
        jLabel97.setText("   21");
        jPanel9.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 40, 30));

        jLabel98.setBackground(new java.awt.Color(13, 47, 90));
        jLabel98.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(204, 204, 204));
        jLabel98.setText("   28");
        jPanel9.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 130, 40, 30));

        jPanel6.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 470, 220));

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel14.setText("Calendar");
        jPanel6.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 90, -1));

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 160, -1, 320));

        jPanel7.setBackground(new java.awt.Color(206, 208, 211));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel14.setBackground(new java.awt.Color(13, 47, 90));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(245, 245, 245));
        jLabel20.setText("November 2026");
        jLabel20.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, 110, -1));

        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(245, 245, 245));
        jLabel21.setText("₱");
        jLabel21.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 20, 30));

        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(245, 245, 245));
        jLabel22.setText("123-456-7890");
        jLabel22.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, 150, 40));

        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 1, 48)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(245, 245, 245));
        jLabel23.setText("3, 000. 50");
        jLabel23.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 260, 70));

        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(245, 245, 245));
        jLabel24.setText("****");
        jLabel24.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 40, 40));

        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(245, 245, 245));
        jLabel27.setText("Total Sales");
        jLabel27.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel14.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 110, -1));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 400, 240));

        jPanel15.setBackground(new java.awt.Color(13, 47, 90));
        jPanel15.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(245, 245, 245));
        jLabel25.setText("Stickers____________________________________________________________ 90");
        jLabel25.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 440, -1));

        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(245, 245, 245));
        jLabel28.setText("Stock Status");
        jLabel28.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 110, -1));

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(245, 245, 245));
        jLabel29.setText("Shirt ______________________________________________________________ 40");
        jLabel29.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 490, -1));

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(245, 245, 245));
        jLabel30.setText("Tote Bag__________________________________________________________ 19");
        jLabel30.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 430, -1));

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(245, 245, 245));
        jLabel31.setText("Button Pins_______________________________________________________ 115");
        jLabel31.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, 430, -1));

        jLabel32.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(245, 245, 245));
        jLabel32.setText("Keychain___________________________________________________________ 35");
        jLabel32.setPreferredSize(new java.awt.Dimension(180, 30));
        jPanel15.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 440, -1));

        jPanel7.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, 510, 250));

        jPanel1.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 160, 610, 590));

        jPanel8.setBackground(new java.awt.Color(206, 208, 211));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel8.setText("Recent Activities");
        jPanel8.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 150, -1));

        jPanel10.setBackground(new java.awt.Color(13, 47, 90));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(204, 204, 204));
        jLabel16.setText("Activity 1");
        jPanel10.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, -1));

        jPanel8.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 460, 40));

        jPanel11.setBackground(new java.awt.Color(13, 47, 90));
        jPanel11.setPreferredSize(new java.awt.Dimension(460, 50));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(204, 204, 204));
        jLabel17.setText("Activity 2");
        jPanel11.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, -1));

        jPanel8.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 460, 40));

        jPanel12.setBackground(new java.awt.Color(13, 47, 90));
        jPanel12.setPreferredSize(new java.awt.Dimension(460, 50));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(204, 204, 204));
        jLabel18.setText("Activity 3");
        jPanel12.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, -1));

        jPanel8.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 40));

        jPanel13.setBackground(new java.awt.Color(13, 47, 90));
        jPanel13.setPreferredSize(new java.awt.Dimension(460, 50));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(204, 204, 204));
        jLabel15.setText("Activity 4");
        jPanel13.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 100, -1));

        jPanel8.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, -1, 40));

        jPanel1.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 490, 520, 260));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1420, 770));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
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
    }//GEN-LAST:event_jButton6ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new DashboardPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jbtnDashBoard;
    // End of variables declaration//GEN-END:variables
}
