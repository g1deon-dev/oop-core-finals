/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalItem;
import com.mycompany.oop.core.finals.model.RentalPayment;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.RentalPaymentService;
import com.mycompany.oop.core.finals.service.RentalService;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Admin
 */
public class RentalServicePanel2 extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RentalServicePanel2.class.getName());
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private UserSession session;
    private RentalService rentalService;
    private RentalPaymentService paymentService;

    /**
     * Creates new form RentalServicePanel2
     */
    public RentalServicePanel2() {
        this(null);
    }

    public RentalServicePanel2(UserSession session) {
        initComponents();
        initBackend(session);
    }

    private void initBackend(UserSession session) {
        this.session = session;
        this.rentalService = new RentalService();
        this.paymentService = new RentalPaymentService();

        polishLayout();
        setupListeners();

        if (session != null && session.isLoggedIn()) {
            jLabel2.setText(session.getCurrentUser().getFullName());
            jLabel1.setText(session.getCurrentUser().getRole().name());
            loadRentals(null, false);
        }
    }

    private void polishLayout() {
        jPanel4.setVisible(false);

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

        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.setBorder(navPadding);
        btnPromos.setMaximumSize(null);
        btnPromos.setMinimumSize(null);
        btnPromos.setPreferredSize(null);
        btnPromos.setForeground(new java.awt.Color(245, 245, 245));

        btnRentalService.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRentalService.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createMatteBorder(0, 3, 0, 0, new java.awt.Color(192, 210, 224)),
            javax.swing.BorderFactory.createEmptyBorder(0, 9, 0, 12)
        ));
        btnRentalService.setMaximumSize(null);
        btnRentalService.setMinimumSize(null);
        btnRentalService.setPreferredSize(null);
        btnRentalService.setOpaque(true);

        jButton6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton6.setBorder(navPadding);
        jButton6.setMaximumSize(null);
        jButton6.setMinimumSize(null);
        jButton6.setPreferredSize(null);

        jButton5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jButton5.setBorder(navPadding);
        jButton5.setMaximumSize(null);
        jButton5.setMinimumSize(null);
        jButton5.setPreferredSize(null);
        jButton5.setForeground(new java.awt.Color(192, 210, 224));

        javax.swing.JLabel lblLogo = new javax.swing.JLabel();
        lblLogo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.net.URL logoUrl = getClass().getResource("/com/mycompany/oop/core/finals/view/logo.png");
        if (logoUrl != null) {
            lblLogo.setIcon(new javax.swing.ImageIcon(logoUrl));
        }

        javax.swing.GroupLayout sidebarLayout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.removeAll();
        jPanel2.setLayout(sidebarLayout);
        sidebarLayout.setHorizontalGroup(
            sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidebarLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(sidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
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
                .addGap(24, 24, 24)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
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
                .addGap(47, 47, 47))
        );

        btnEdit.setText("Sort by ID");
        btnEdit1.setText("Sort by Name");

        pack();
        setLocationRelativeTo(null);
    }

    private void setupListeners() {
        btnDashboard.addActionListener(e -> {
            new DashboardPanel(session).setVisible(true);
            this.dispose();
        });
        btnMerchInventory.addActionListener(e -> {
            new MerchInventoryPanel(session).setVisible(true);
            this.dispose();
        });
        btnPromos.addActionListener(e -> {
            new PromosPanel(session).setVisible(true);
            this.dispose();
        });
        btnRentalService.addActionListener(e -> {
            new RentalServicePanel(session).setVisible(true);
            this.dispose();
        });
        jButton6.addActionListener(e -> showAccountSettings());
        jButton1.addActionListener(e -> {
            new RentalServicePanel(session).setVisible(true);
            this.dispose();
        });
        jButton2.addActionListener(e -> loadRentals(txtSearch.getText().trim(), false));

        btnSearch.addActionListener(e -> loadRentals(txtSearch.getText().trim(), false));
        txtSearch.addActionListener(e -> loadRentals(txtSearch.getText().trim(), false));
        btnEdit.addActionListener(e -> loadRentals(txtSearch.getText().trim(), false));
        btnEdit1.addActionListener(e -> loadRentals(txtSearch.getText().trim(), true));

        setupTableContextMenu();
    }

    private void setupTableContextMenu() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem miDetails = new JMenuItem("View Items & Payments");
        miDetails.addActionListener(e -> handleViewDetails());

        JMenuItem miStart = new JMenuItem("Start Rental (Mark ONGOING)");
        miStart.addActionListener(e -> handleStatusAction("START"));

        JMenuItem miReturn = new JMenuItem("Mark Returned");
        miReturn.addActionListener(e -> handleStatusAction("RETURN"));

        JMenuItem miCancel = new JMenuItem("Cancel Rental");
        miCancel.addActionListener(e -> handleStatusAction("CANCEL"));

        JMenuItem miPay = new JMenuItem("Mark Pending Payment Paid");
        miPay.addActionListener(e -> handleMarkPaid());

        menu.add(miDetails);
        menu.addSeparator();
        menu.add(miStart);
        menu.add(miReturn);
        menu.add(miCancel);
        menu.addSeparator();
        menu.add(miPay);

        tblProducts.setComponentPopupMenu(menu);
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = tblProducts.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tblProducts.getRowCount()) {
                    tblProducts.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
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
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jPanel11 = new javax.swing.JPanel();
        lblProductManagement1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        btnEdit = new javax.swing.JButton();
        btnEdit1 = new javax.swing.JButton();

        jLabel4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(245, 245, 245));
        jLabel4.setText("Create merchandise bundles, manage promotional offers, and monitor customer pre-orders.");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1280, 800));

        jPanel2.setBackground(new java.awt.Color(30, 30, 30));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
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
        btnPromos.setBorder(null);
        btnPromos.setContentAreaFilled(false);
        btnPromos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPromos.setMaximumSize(new java.awt.Dimension(90, 20));
        btnPromos.setMinimumSize(new java.awt.Dimension(90, 20));
        btnPromos.setPreferredSize(new java.awt.Dimension(90, 20));

        btnRentalService.setBackground(new java.awt.Color(11, 52, 90));
        btnRentalService.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnRentalService.setForeground(new java.awt.Color(245, 245, 245));
        btnRentalService.setText("RENTAL SERVICE");
        btnRentalService.setBorder(javax.swing.BorderFactory.createEtchedBorder());
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRentalService, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnMerchInventory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .addComponent(btnDashboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(btnPromos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(199, 199, 199)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMerchInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRentalService, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 321, Short.MAX_VALUE)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        jPanel3.setBackground(new java.awt.Color(9, 30, 57));
        jPanel3.setPreferredSize(new java.awt.Dimension(1035, 130));

        lblPromos.setFont(new java.awt.Font("Agency FB", 1, 50)); // NOI18N
        lblPromos.setForeground(new java.awt.Color(245, 245, 245));
        lblPromos.setText("Rental Service");
        lblPromos.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        jLabel5.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(245, 245, 245));
        jLabel5.setText("Manage equipment rentals, monitor booking statuses, and track equipment returns.\n");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(245, 245, 245));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Elijah Yanto");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(245, 245, 245));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Technicals");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPromos, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 804, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 316, Short.MAX_VALUE)
        );

        jButton1.setBackground(new java.awt.Color(245, 245, 245));
        jButton1.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(33, 86, 124));
        jButton1.setText("Rental Booking");

        jButton2.setBackground(new java.awt.Color(9, 30, 57));
        jButton2.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(245, 245, 245));
        jButton2.setText("Rental Records");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBackground(new java.awt.Color(227, 232, 238));

        txtSearch.setBackground(new java.awt.Color(245, 245, 245));

        btnSearch.setBackground(new java.awt.Color(163, 190, 211));
        btnSearch.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        btnSearch.setForeground(new java.awt.Color(76, 129, 163));
        btnSearch.setText("Search");

        jPanel11.setBackground(new java.awt.Color(9, 30, 57));

        lblProductManagement1.setFont(new java.awt.Font("Segoe UI Historic", 1, 24)); // NOI18N
        lblProductManagement1.setForeground(new java.awt.Color(245, 245, 245));
        lblProductManagement1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductManagement1.setText("RENTAL RECORDS\n");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(326, Short.MAX_VALUE)
                .addComponent(lblProductManagement1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(314, 314, 314))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblProductManagement1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(null);

        tblProducts.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        tblProducts.setForeground(new java.awt.Color(33, 86, 124));
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Rental ID", "Customer Name", "Equipment", "Start Time", "Return Time", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        btnEdit.setBackground(new java.awt.Color(222, 239, 255));
        btnEdit.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnEdit.setForeground(new java.awt.Color(2, 133, 255));
        btnEdit.setText("By Product ID");

        btnEdit1.setBackground(new java.awt.Color(254, 225, 224));
        btnEdit1.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnEdit1.setForeground(new java.awt.Color(224, 46, 42));
        btnEdit1.setText("By Name\n");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(1, 1, 1))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEdit1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 964, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEdit)
                    .addComponent(btnEdit1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(139, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        int confirm = JOptionPane.showConfirmDialog(
            this, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (session != null) session.endSession();
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void loadRentals(String keyword, boolean sortByName) {
        try {
            List<Rental> rentals = (keyword == null || keyword.isBlank())
                    ? rentalService.getAllRentals()
                    : rentalService.searchRentals(keyword.trim());

            if (sortByName) {
                rentals.sort(Comparator.comparing(
                        r -> r.getCustomerName() == null ? "" : r.getCustomerName(),
                        String.CASE_INSENSITIVE_ORDER));
            } else {
                rentals.sort(Comparator.comparingInt(Rental::getRentalId).reversed());
            }

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Rental ID", "Customer Name", "Equipment", "Start Time", "Return Time", "Status"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Rental r : rentals) {
                String equipSummary = "-";
                try {
                    List<RentalItem> items = rentalService.getRentalItems(r.getRentalId());
                    if (!items.isEmpty()) {
                        equipSummary = items.stream()
                                .map(it -> it.getProductName() + (it.getQuantity() > 1 ? " x" + it.getQuantity() : ""))
                                .collect(Collectors.joining(", "));
                    }
                } catch (SQLException ignored) {
                }

                String startStr = r.getRentalStart() != null ? r.getRentalStart().format(DT_FMT) : "-";
                String returnStr = r.getActualReturn() != null
                        ? r.getActualReturn().format(DT_FMT)
                        : (r.getExpectedReturn() != null ? r.getExpectedReturn().format(DT_FMT) : "-");
                String statusStr = r.getStatus() != null ? r.getStatus().name() : "-";
                if (rentalService.isOverdue(r)) {
                    statusStr = statusStr + " (OVERDUE)";
                }

                model.addRow(new Object[]{
                    r.getRentalId(),
                    r.getCustomerName(),
                    equipSummary,
                    startStr,
                    returnStr,
                    statusStr
                });
            }
            tblProducts.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load rentals: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Integer getSelectedRentalId() {
        int row = tblProducts.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a rental row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Object val = tblProducts.getValueAt(row, 0);
        if (val instanceof Integer) {
            return (Integer) val;
        }
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void handleViewDetails() {
        Integer rentalId = getSelectedRentalId();
        if (rentalId == null) return;
        try {
            Rental r = rentalService.getRentalById(rentalId);
            if (r == null) return;
            List<RentalItem> items = rentalService.getRentalItems(rentalId);
            List<RentalPayment> payments = paymentService.getPaymentsByRentalId(rentalId);

            StringBuilder sb = new StringBuilder();
            sb.append("Rental #").append(r.getRentalId()).append(" (").append(r.getStatus()).append(")\n");
            sb.append("Customer: ").append(r.getCustomerName()).append("\n");
            sb.append("Contact: ").append(r.getContactNumber()).append("\n");
            sb.append("Venue: ").append(r.getVenue()).append("\n\n");
            sb.append("Items:\n");
            for (RentalItem it : items) {
                sb.append(String.format(" - %s x%d (PHP %,.2f)\n",
                        it.getProductName(), it.getQuantity(), it.getTotalPriceCentavos() / 100.0));
            }
            sb.append("\nPayments:\n");
            if (payments.isEmpty()) {
                sb.append(" - None recorded\n");
            } else {
                for (RentalPayment p : payments) {
                    sb.append(String.format(" - Payment #%d: PHP %,.2f [%s] (%s)\n",
                            p.getPaymentId(), p.getAmountCentavos() / 100.0, p.getPaymentMethod(), p.getPaymentStatus()));
                }
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Rental Details", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStatusAction(String action) {
        Integer rentalId = getSelectedRentalId();
        if (rentalId == null) return;
        try {
            switch (action) {
                case "START" -> rentalService.startRental(rentalId);
                case "RETURN" -> rentalService.returnRental(rentalId);
                case "CANCEL" -> rentalService.cancelRental(rentalId);
            }
            JOptionPane.showMessageDialog(this, "Rental #" + rentalId + " updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadRentals(txtSearch.getText().trim(), false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Action Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleMarkPaid() {
        Integer rentalId = getSelectedRentalId();
        if (rentalId == null) return;
        try {
            List<RentalPayment> payments = paymentService.getPaymentsByRentalId(rentalId);
            RentalPayment pending = payments.stream()
                    .filter(p -> !"PAID".equalsIgnoreCase(p.getPaymentStatus()))
                    .findFirst()
                    .orElse(null);
            if (pending == null) {
                JOptionPane.showMessageDialog(this, "No pending payment found for Rental #" + rentalId + ".", "Payments", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            paymentService.markPaymentAsPaid(pending.getPaymentId());
            JOptionPane.showMessageDialog(this,
                    String.format("Payment #%d (PHP %,.2f) marked as PAID.",
                            pending.getPaymentId(), pending.getAmountCentavos() / 100.0),
                    "Payment Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAccountSettings() {
        if (session == null || !session.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Not logged in.", "Settings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String info = "User: " + session.getCurrentUser().getFullName()
                + "\nUsername: " + session.getCurrentUser().getUsername()
                + "\nRole: " + session.getCurrentUser().getRole().name();
        JOptionPane.showMessageDialog(this, info, "Account & System Info", JOptionPane.INFORMATION_MESSAGE);
    }

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
        java.awt.EventQueue.invokeLater(() -> new RentalServicePanel2().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEdit1;
    private javax.swing.JButton btnMerchInventory;
    private javax.swing.JButton btnPromos;
    private javax.swing.JButton btnRentalService;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblProductManagement1;
    private javax.swing.JLabel lblPromos;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
