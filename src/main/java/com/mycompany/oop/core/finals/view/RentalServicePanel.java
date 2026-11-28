/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalItem;
import com.mycompany.oop.core.finals.model.RentalStatus;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.RentalPaymentService;
import com.mycompany.oop.core.finals.service.RentalService;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Admin
 */
public class RentalServicePanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RentalServicePanel.class.getName());
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private UserSession session;
    private RentalService rentalService;
    private RentalPaymentService paymentService;

    private final Map<String, Integer> equipmentRatesCentavos = new LinkedHashMap<>();
    private final List<Rental> displayedPendingRentals = new ArrayList<>();

    /**
     * Creates new form RentalServicePanel
     */
    public RentalServicePanel() {
        this(null);
    }

    public RentalServicePanel(UserSession session) {
        initComponents();
        initBackend(session);
    }

    private void initBackend(UserSession session) {
        this.session = session;
        this.rentalService = new RentalService();
        this.paymentService = new RentalPaymentService();

        polishLayout();
        populateFormOptions();
        setupListeners();

        if (session != null && session.isLoggedIn()) {
            jLabel2.setText(session.getCurrentUser().getFullName());
            jLabel1.setText(session.getCurrentUser().getRole().name());
            loadPendingCancellations();
        } else {
            clearCancellationCards();
        }
    }

    private void polishLayout() {
        jLabel5.setText("Manage equipment rentals, monitor booking statuses, and track equipment returns.");
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

        JLabel lblLogo = new JLabel();
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

        jLabel6.setFont(new java.awt.Font("Segoe UI Historic", java.awt.Font.BOLD, 14));
        jLabel6.setText("Total Amount: PHP 0.00");

        pack();
        setLocationRelativeTo(null);
    }

    private void populateFormOptions() {
        equipmentRatesCentavos.clear();
        equipmentRatesCentavos.put("Speakers(2) + Sound System + Wireless Mics(2 with receiver) - Inside BUCS (PHP 350.00/day)", 35000);
        equipmentRatesCentavos.put("Speakers(2) + Sound System + Wireless Mics(2 with receiver) - Outside BUCS (PHP 450.00/day)", 45000);

        DefaultComboBoxModel<String> equipModel = new DefaultComboBoxModel<>();
        equipModel.addElement("-- Select Equipment --");
        for (String label : equipmentRatesCentavos.keySet()) {
            equipModel.addElement(label);
        }
        jComboBox1.setModel(equipModel);

        List<String> dates = new ArrayList<>();
        dates.add("-- Select Date --");
        LocalDate today = LocalDate.now();
        for (int i = 0; i <= 30; i++) {
            dates.add(today.plusDays(i).format(DATE_FMT));
        }
        jComboBox2.setModel(new DefaultComboBoxModel<>(dates.toArray(new String[0])));
        jComboBox4.setModel(new DefaultComboBoxModel<>(dates.toArray(new String[0])));
        jComboBox6.setModel(new DefaultComboBoxModel<>(dates.toArray(new String[0])));
        jComboBox6.setSelectedIndex(1); // default actual return to today

        List<String> times = new ArrayList<>();
        times.add("--Time--");
        for (int h = 8; h <= 20; h++) {
            times.add(String.format("%02d:00", h));
        }
        jComboBox3.setModel(new DefaultComboBoxModel<>(times.toArray(new String[0])));
        jComboBox5.setModel(new DefaultComboBoxModel<>(times.toArray(new String[0])));
        jComboBox7.setModel(new DefaultComboBoxModel<>(times.toArray(new String[0])));
        jComboBox7.setSelectedItem("12:00");

        jComboBox8.setModel(new DefaultComboBoxModel<>(new String[] {
            "-- Select Condition --", "Good", "Minor Wear", "Damaged"
        }));
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
        btnRentalService.addActionListener(e -> loadPendingCancellations());
        jButton6.addActionListener(e -> showAccountSettings());
        jButton2.addActionListener(e -> {
            new RentalServicePanel2(session).setVisible(true);
            this.dispose();
        });

        jComboBox1.addActionListener(e -> updateTotalAmount());
        jComboBox2.addActionListener(e -> updateTotalAmount());
        jComboBox3.addActionListener(e -> updateTotalAmount());
        jComboBox4.addActionListener(e -> updateTotalAmount());
        jComboBox5.addActionListener(e -> updateTotalAmount());

        jButton3.addActionListener(e -> handleSaveBooking());
        jButton10.addActionListener(e -> handleRecordReturn());

        jButton7.addActionListener(e -> handleCancellationAction(0, true));
        jButton4.addActionListener(e -> handleCancellationAction(0, false));
        jButton8.addActionListener(e -> handleCancellationAction(1, true));
        jButton9.addActionListener(e -> handleCancellationAction(1, false));
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
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btnRenterName = new javax.swing.JButton();
        btnRenterName1 = new javax.swing.JButton();
        btnRenterName2 = new javax.swing.JButton();
        btnRenterName3 = new javax.swing.JButton();
        btnRenterName4 = new javax.swing.JButton();
        txtRenterName = new javax.swing.JTextField();
        txtContactNum = new javax.swing.JTextField();
        txtRenterAddress = new javax.swing.JTextField();
        txtEventVenue = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        btnRenterName5 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        btnRenterName6 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        btnRenterName7 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox<>();
        btnRenterName8 = new javax.swing.JButton();
        btnRenterName9 = new javax.swing.JButton();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        pnlPreOrder = new javax.swing.JLabel();
        lbOrderId = new javax.swing.JLabel();
        lbOrderId1 = new javax.swing.JLabel();
        lbOrderId2 = new javax.swing.JLabel();
        lbOrderId3 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lbOrderId4 = new javax.swing.JLabel();
        lbOrderId5 = new javax.swing.JLabel();
        lbOrderId6 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        lbOrderId7 = new javax.swing.JLabel();
        lbOrderId8 = new javax.swing.JLabel();
        lbOrderId9 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        pnlPreOrder1 = new javax.swing.JLabel();
        btnRenterName10 = new javax.swing.JButton();
        txtContactNum1 = new javax.swing.JTextField();
        A = new javax.swing.JButton();
        jComboBox6 = new javax.swing.JComboBox<>();
        jComboBox7 = new javax.swing.JComboBox<>();
        Condition = new javax.swing.JButton();
        jComboBox8 = new javax.swing.JComboBox<>();
        Condition1 = new javax.swing.JButton();
        txtContactNum2 = new javax.swing.JTextField();
        Condition2 = new javax.swing.JButton();
        Condition3 = new javax.swing.JButton();
        txtContactNum3 = new javax.swing.JTextField();
        txtContactNum4 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();

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
        jLabel5.setText("Create merchandise bundles, manage promotional offers, and monitor customer pre-orders.");
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

        jButton1.setBackground(new java.awt.Color(9, 30, 57));
        jButton1.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(245, 245, 245));
        jButton1.setText("Rental Booking");

        jButton2.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(33, 86, 124));
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

        jPanel6.setBackground(new java.awt.Color(227, 232, 238));

        jPanel7.setBackground(new java.awt.Color(191, 205, 221));

        jLabel3.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 86, 124));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("RENTAL BOOKING FORM");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(98, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        btnRenterName.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName.setText("Renter Name:");
        btnRenterName.setBorder(null);
        btnRenterName.setContentAreaFilled(false);
        btnRenterName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName.addActionListener(this::btnRenterNameActionPerformed);

        btnRenterName1.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName1.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName1.setText("Contact Number:");
        btnRenterName1.setBorder(null);
        btnRenterName1.setContentAreaFilled(false);
        btnRenterName1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName1.addActionListener(this::btnRenterName1ActionPerformed);

        btnRenterName2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName2.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName2.setText("Rental Purpose:");
        btnRenterName2.setBorder(null);
        btnRenterName2.setContentAreaFilled(false);
        btnRenterName2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName2.addActionListener(this::btnRenterName2ActionPerformed);

        btnRenterName3.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName3.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName3.setText("Event Venue/Location:");
        btnRenterName3.setBorder(null);
        btnRenterName3.setContentAreaFilled(false);
        btnRenterName3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName3.addActionListener(this::btnRenterName3ActionPerformed);

        btnRenterName4.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName4.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName4.setText("Renter Adress");
        btnRenterName4.setBorder(null);
        btnRenterName4.setContentAreaFilled(false);
        btnRenterName4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName4.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName4.addActionListener(this::btnRenterName4ActionPerformed);

        txtRenterName.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtRenterName.setForeground(new java.awt.Color(33, 86, 124));

        txtContactNum.setForeground(new java.awt.Color(33, 86, 124));

        txtRenterAddress.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtRenterAddress.setForeground(new java.awt.Color(33, 86, 124));

        txtEventVenue.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtEventVenue.setForeground(new java.awt.Color(33, 86, 124));

        jTextField5.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(33, 86, 124));

        btnRenterName5.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName5.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName5.setText("Select an Equipment:");
        btnRenterName5.setBorder(null);
        btnRenterName5.setContentAreaFilled(false);
        btnRenterName5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName5.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName5.addActionListener(this::btnRenterName5ActionPerformed);

        jComboBox1.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Equipment --", "Speaker", "Sound System with Microphone", " " }));

        btnRenterName6.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName6.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName6.setText("Pick-up Date:");
        btnRenterName6.setBorder(null);
        btnRenterName6.setContentAreaFilled(false);
        btnRenterName6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName6.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName6.addActionListener(this::btnRenterName6ActionPerformed);

        jComboBox2.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox2.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Date --" }));

        btnRenterName7.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName7.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName7.setText("Time:");
        btnRenterName7.setBorder(null);
        btnRenterName7.setContentAreaFilled(false);
        btnRenterName7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName7.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName7.addActionListener(this::btnRenterName7ActionPerformed);

        jComboBox3.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox3.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Time--", " " }));
        jComboBox3.addActionListener(this::jComboBox3ActionPerformed);

        btnRenterName8.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName8.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName8.setText("Return Date:");
        btnRenterName8.setBorder(null);
        btnRenterName8.setContentAreaFilled(false);
        btnRenterName8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName8.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName8.addActionListener(this::btnRenterName8ActionPerformed);

        btnRenterName9.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName9.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName9.setText("Time:");
        btnRenterName9.setBorder(null);
        btnRenterName9.setContentAreaFilled(false);
        btnRenterName9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName9.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName9.addActionListener(this::btnRenterName9ActionPerformed);

        jComboBox4.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox4.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Date --" }));

        jComboBox5.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox5.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Time--", " ", " " }));

        jPanel8.setForeground(new java.awt.Color(33, 86, 124));

        jLabel6.setForeground(new java.awt.Color(33, 86, 124));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Total Amount");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jButton3.setBackground(new java.awt.Color(163, 190, 211));
        jButton3.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(76, 129, 163));
        jButton3.setText("Save Receipt");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                            .addGap(10, 10, 10)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                        .addComponent(btnRenterName, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(231, 231, 231)
                                        .addComponent(btnRenterName1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnRenterName4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addGap(6, 6, 6)
                                    .addComponent(txtRenterName, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(23, 23, 23)
                                    .addComponent(txtContactNum))))
                        .addGroup(jPanel6Layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(txtRenterAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnRenterName3, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(txtEventVenue, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(btnRenterName2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(btnRenterName5, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(117, 117, 117))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(btnRenterName6, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnRenterName7, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(btnRenterName8, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnRenterName9, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addGap(57, 57, 57)
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRenterName1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenterName, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRenterName, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtContactNum, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRenterName4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRenterAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRenterName3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtEventVenue, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRenterName2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRenterName5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenterName6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRenterName7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRenterName9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRenterName8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(227, 232, 238));

        jPanel11.setBackground(new java.awt.Color(9, 30, 57));

        pnlPreOrder.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        pnlPreOrder.setForeground(new java.awt.Color(245, 245, 245));
        pnlPreOrder.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlPreOrder.setText("CANCELLATION REQUEST");
        pnlPreOrder.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .addComponent(pnlPreOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPreOrder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        lbOrderId.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId.setText("Rental ID");

        lbOrderId1.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId1.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId1.setText("Customer Name");

        lbOrderId2.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId2.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId2.setText("Reason");

        lbOrderId3.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId3.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId3.setText("Action");

        lbOrderId4.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId4.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId4.setText("SP-01");

        lbOrderId5.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId5.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId5.setText("Frich Ramos");

        lbOrderId6.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId6.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId6.setText("Not Needed ");

        jButton7.setBackground(new java.awt.Color(218, 245, 228));
        jButton7.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(21, 128, 61));
        jButton7.setText("✔");

        jButton4.setBackground(new java.awt.Color(254, 225, 224));
        jButton4.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(224, 46, 42));
        jButton4.setText("✘");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOrderId4, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbOrderId5, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbOrderId6, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderId4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        lbOrderId7.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId7.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId7.setText("SP-01");

        lbOrderId8.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId8.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId8.setText("Frich Ramos");

        lbOrderId9.setFont(new java.awt.Font("Segoe UI Historic", 0, 14)); // NOI18N
        lbOrderId9.setForeground(new java.awt.Color(33, 86, 124));
        lbOrderId9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbOrderId9.setText("Not Needed ");

        jButton8.setBackground(new java.awt.Color(218, 245, 228));
        jButton8.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        jButton8.setForeground(new java.awt.Color(21, 128, 61));
        jButton8.setText("✔");

        jButton9.setBackground(new java.awt.Color(254, 225, 224));
        jButton9.setFont(new java.awt.Font("Segoe UI Emoji", 1, 12)); // NOI18N
        jButton9.setForeground(new java.awt.Color(224, 46, 42));
        jButton9.setText("✘");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbOrderId7, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbOrderId8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbOrderId9, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderId7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lbOrderId, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbOrderId1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbOrderId2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbOrderId3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbOrderId, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId2, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbOrderId3, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(66, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(227, 232, 238));

        jPanel14.setBackground(new java.awt.Color(191, 205, 221));

        pnlPreOrder1.setFont(new java.awt.Font("Segoe UI Historic", 1, 14)); // NOI18N
        pnlPreOrder1.setForeground(new java.awt.Color(33, 86, 124));
        pnlPreOrder1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pnlPreOrder1.setText("RETURN ITEM FORM");
        pnlPreOrder1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(95, Short.MAX_VALUE)
                .addComponent(pnlPreOrder1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPreOrder1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        btnRenterName10.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        btnRenterName10.setForeground(new java.awt.Color(33, 86, 124));
        btnRenterName10.setText("Rental ID:");
        btnRenterName10.setBorder(null);
        btnRenterName10.setContentAreaFilled(false);
        btnRenterName10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRenterName10.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        btnRenterName10.addActionListener(this::btnRenterName10ActionPerformed);

        txtContactNum1.setForeground(new java.awt.Color(33, 86, 124));

        A.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        A.setForeground(new java.awt.Color(33, 86, 124));
        A.setText("Actual Return Date:");
        A.setBorder(null);
        A.setContentAreaFilled(false);
        A.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        A.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        A.addActionListener(this::AActionPerformed);

        jComboBox6.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox6.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Date --" }));

        jComboBox7.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox7.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Time--", " " }));
        jComboBox7.addActionListener(this::jComboBox7ActionPerformed);

        Condition.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        Condition.setForeground(new java.awt.Color(33, 86, 124));
        Condition.setText("Condition:");
        Condition.setBorder(null);
        Condition.setContentAreaFilled(false);
        Condition.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Condition.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        Condition.addActionListener(this::ConditionActionPerformed);

        jComboBox8.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        jComboBox8.setForeground(new java.awt.Color(33, 86, 124));
        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select Condition--" }));

        Condition1.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        Condition1.setForeground(new java.awt.Color(33, 86, 124));
        Condition1.setText("Late Fee:");
        Condition1.setBorder(null);
        Condition1.setContentAreaFilled(false);
        Condition1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Condition1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        Condition1.addActionListener(this::Condition1ActionPerformed);

        txtContactNum2.setForeground(new java.awt.Color(33, 86, 124));

        Condition2.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        Condition2.setForeground(new java.awt.Color(33, 86, 124));
        Condition2.setText("Remarks:");
        Condition2.setBorder(null);
        Condition2.setContentAreaFilled(false);
        Condition2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Condition2.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        Condition2.addActionListener(this::Condition2ActionPerformed);

        Condition3.setFont(new java.awt.Font("Segoe UI Historic", 1, 12)); // NOI18N
        Condition3.setForeground(new java.awt.Color(33, 86, 124));
        Condition3.setText("Repair Fee:");
        Condition3.setBorder(null);
        Condition3.setContentAreaFilled(false);
        Condition3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        Condition3.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        Condition3.addActionListener(this::Condition3ActionPerformed);

        txtContactNum3.setForeground(new java.awt.Color(33, 86, 124));

        txtContactNum4.setForeground(new java.awt.Color(33, 86, 124));

        jButton10.setBackground(new java.awt.Color(163, 190, 211));
        jButton10.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        jButton10.setForeground(new java.awt.Color(76, 129, 163));
        jButton10.setText("Record Return");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(A, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox7, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(btnRenterName10, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtContactNum1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(Condition, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox8, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(Condition2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtContactNum2))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(Condition1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtContactNum3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(Condition3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtContactNum4)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(136, 136, 136))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRenterName10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtContactNum1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                    .addComponent(jComboBox7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Condition, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtContactNum2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Condition2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtContactNum3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Condition1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Condition3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtContactNum4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void btnRenterNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterNameActionPerformed

    private void btnRenterName1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName1ActionPerformed

    private void btnRenterName2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName2ActionPerformed

    private void btnRenterName3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName3ActionPerformed

    private void btnRenterName4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName4ActionPerformed

    private void btnRenterName5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName5ActionPerformed

    private void btnRenterName6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName6ActionPerformed

    private void btnRenterName7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName7ActionPerformed

    private void btnRenterName8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName8ActionPerformed

    private void btnRenterName9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName9ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void btnRenterName10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRenterName10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRenterName10ActionPerformed

    private void AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AActionPerformed

    private void jComboBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox7ActionPerformed

    private void ConditionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConditionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConditionActionPerformed

    private void Condition1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Condition1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Condition1ActionPerformed

    private void Condition2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Condition2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Condition2ActionPerformed

    private void Condition3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Condition3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Condition3ActionPerformed

    private int calculateTotalCentavos() {
        String selectedEquip = (String) jComboBox1.getSelectedItem();
        if (selectedEquip == null || !equipmentRatesCentavos.containsKey(selectedEquip)) {
            return 0;
        }
        int dailyRate = equipmentRatesCentavos.get(selectedEquip);
        LocalDateTime start = parseDateTime(jComboBox2, jComboBox3);
        LocalDateTime end = parseDateTime(jComboBox4, jComboBox5);
        long days = 1;
        if (start != null && end != null && end.isAfter(start)) {
            long between = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
            days = Math.max(1, between);
        }
        return (int) (dailyRate * days);
    }

    private void updateTotalAmount() {
        int totalCentavos = calculateTotalCentavos();
        jLabel6.setText(String.format("Total Amount: PHP %,.2f", totalCentavos / 100.0));
    }

    private LocalDateTime parseDateTime(JComboBox<String> dateBox, JComboBox<String> timeBox) {
        Object dObj = dateBox.getSelectedItem();
        Object tObj = timeBox.getSelectedItem();
        if (dObj == null || tObj == null) {
            return null;
        }
        String dStr = dObj.toString().trim();
        String tStr = tObj.toString().trim();
        if (dStr.startsWith("--") || tStr.startsWith("--") || dStr.isEmpty() || tStr.isEmpty()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(dStr, DATE_FMT);
            LocalTime time = LocalTime.parse(tStr, TIME_FMT);
            return LocalDateTime.of(date, time);
        } catch (Exception ex) {
            return null;
        }
    }

    private void handleSaveBooking() {
        String renterName = txtRenterName.getText().trim();
        String contactNum = txtContactNum.getText().trim();
        String venue = txtEventVenue.getText().trim();
        String selectedEquip = (String) jComboBox1.getSelectedItem();

        if (renterName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Renter name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtRenterName.requestFocus();
            return;
        }
        if (selectedEquip == null || !equipmentRatesCentavos.containsKey(selectedEquip)) {
            JOptionPane.showMessageDialog(this, "Please select equipment to rent.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime start = parseDateTime(jComboBox2, jComboBox3);
        LocalDateTime expectedReturn = parseDateTime(jComboBox4, jComboBox5);
        if (start == null || expectedReturn == null) {
            JOptionPane.showMessageDialog(this, "Please select both pick-up and return dates and times.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!expectedReturn.isAfter(start)) {
            JOptionPane.showMessageDialog(this, "Return date/time must be after pick-up date/time.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String equipName = selectedEquip.contains(" (")
                ? selectedEquip.substring(0, selectedEquip.indexOf(" ("))
                : selectedEquip;
        int totalCentavos = calculateTotalCentavos();

        String[] methods = {"CASH", "GCASH", "BANK_TRANSFER"};
        JComboBox<String> cmbMethod = new JComboBox<>(methods);
        JComboBox<String> cmbPaid = new JComboBox<>(new String[]{"Paid Now", "Pay Later (Pending)"});
        JPanel payPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        payPanel.add(new JLabel("Total Amount:"));
        payPanel.add(new JLabel(String.format("PHP %,.2f", totalCentavos / 100.0)));
        payPanel.add(new JLabel("Payment Method:"));
        payPanel.add(cmbMethod);
        payPanel.add(new JLabel("Payment Status:"));
        payPanel.add(cmbPaid);

        int confirm = JOptionPane.showConfirmDialog(
                this, payPanel, "Confirm Rental Booking", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        String paymentMethod = (String) cmbMethod.getSelectedItem();
        boolean paidNow = cmbPaid.getSelectedIndex() == 0;

        List<RentalItem> items = new ArrayList<>();
        items.add(new RentalItem(0, 0, 0, equipName, 1, totalCentavos));

        try {
            int rentalId = rentalService.createRental(
                    renterName, contactNum, venue, start, expectedReturn, items, paymentMethod, paidNow);
            JOptionPane.showMessageDialog(this,
                    String.format("Rental #%d saved!\nRenter: %s\nEquipment: %s\nTotal: PHP %,.2f (%s)",
                            rentalId, renterName, equipName, totalCentavos / 100.0,
                            paidNow ? "PAID" : "PENDING"),
                    "Booking Saved",
                    JOptionPane.INFORMATION_MESSAGE);
            clearBookingForm();
            loadPendingCancellations();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearBookingForm() {
        txtRenterName.setText("");
        txtContactNum.setText("");
        txtRenterAddress.setText("");
        txtEventVenue.setText("");
        jTextField5.setText("");
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(0);
        updateTotalAmount();
    }

    private void handleRecordReturn() {
        String idText = txtContactNum1.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Rental ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtContactNum1.requestFocus();
            return;
        }
        int rentalId;
        try {
            rentalId = Integer.parseInt(idText.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Rental ID must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int lateFeeCentavos = parsePesosToCentavos(txtContactNum3.getText().trim(), "Late Fee");
        if (lateFeeCentavos < 0) return;
        int repairFeeCentavos = parsePesosToCentavos(txtContactNum4.getText().trim(), "Repair Fee");
        if (repairFeeCentavos < 0) return;

        try {
            Rental rental = rentalService.getRentalById(rentalId);
            if (rental == null) {
                JOptionPane.showMessageDialog(this, "Rental #" + rentalId + " was not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (rental.getStatus() == RentalStatus.RETURNED) {
                JOptionPane.showMessageDialog(this, "Rental #" + rentalId + " is already marked as RETURNED.", "Already Returned", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (rental.getStatus() == RentalStatus.CANCELLED) {
                JOptionPane.showMessageDialog(this, "Rental #" + rentalId + " was CANCELLED and cannot be returned.", "Cancelled", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (rental.getStatus() == RentalStatus.RESERVED) {
                rentalService.startRental(rentalId);
            }
            rentalService.returnRental(rentalId);

            int totalExtraFees = lateFeeCentavos + repairFeeCentavos;
            if (totalExtraFees > 0) {
                paymentService.recordPayment(rentalId, totalExtraFees, "CASH", true);
            }

            String condition = (String) jComboBox8.getSelectedItem();
            String remarks = txtContactNum2.getText().trim();
            JOptionPane.showMessageDialog(this,
                    String.format("Rental #%d marked as RETURNED.\nCondition: %s\nExtra Fees Recorded: PHP %,.2f%s",
                            rentalId,
                            (condition == null || condition.startsWith("--")) ? "Not specified" : condition,
                            totalExtraFees / 100.0,
                            remarks.isEmpty() ? "" : "\nRemarks: " + remarks),
                    "Return Recorded",
                    JOptionPane.INFORMATION_MESSAGE);

            txtContactNum1.setText("");
            txtContactNum2.setText("");
            txtContactNum3.setText("");
            txtContactNum4.setText("");
            jComboBox8.setSelectedIndex(0);
            loadPendingCancellations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error recording return: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parsePesosToCentavos(String text, String fieldName) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            double pesos = Double.parseDouble(text.replace(",", "").trim());
            if (pesos < 0) {
                JOptionPane.showMessageDialog(this, fieldName + " cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return -1;
            }
            return (int) Math.round(pesos * 100.0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, fieldName + " must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
    }

    private void loadPendingCancellations() {
        displayedPendingRentals.clear();
        try {
            List<Rental> reserved = rentalService.getRentalsByStatus(RentalStatus.RESERVED);
            for (int i = 0; i < Math.min(2, reserved.size()); i++) {
                displayedPendingRentals.add(reserved.get(i));
            }
        } catch (SQLException ex) {
            logger.warning("Could not load reserved rentals: " + ex.getMessage());
        }

        if (displayedPendingRentals.size() >= 1) {
            Rental r = displayedPendingRentals.get(0);
            jPanel10.setVisible(true);
            lbOrderId4.setText("R-" + r.getRentalId());
            lbOrderId5.setText(r.getCustomerName());
            lbOrderId6.setText("RESERVED");
        } else {
            jPanel10.setVisible(true);
            lbOrderId4.setText("-");
            lbOrderId5.setText("No pending rentals");
            lbOrderId6.setText("-");
        }

        if (displayedPendingRentals.size() >= 2) {
            Rental r = displayedPendingRentals.get(1);
            jPanel12.setVisible(true);
            lbOrderId7.setText("R-" + r.getRentalId());
            lbOrderId8.setText(r.getCustomerName());
            lbOrderId9.setText("RESERVED");
        } else {
            jPanel12.setVisible(false);
        }
    }

    private void clearCancellationCards() {
        displayedPendingRentals.clear();
        lbOrderId4.setText("-");
        lbOrderId5.setText("Not connected");
        lbOrderId6.setText("-");
        jPanel12.setVisible(false);
    }

    private void handleCancellationAction(int slotIndex, boolean cancelRental) {
        if (slotIndex >= displayedPendingRentals.size()) {
            JOptionPane.showMessageDialog(this, "No reserved rental in this slot.", "Rental Service", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Rental rental = displayedPendingRentals.get(slotIndex);
        String actionText = cancelRental
                ? "Cancel Rental #" + rental.getRentalId() + " (" + rental.getCustomerName() + ")?"
                : "Start Rental #" + rental.getRentalId() + " (" + rental.getCustomerName() + ") as ONGOING?";
        int confirm = JOptionPane.showConfirmDialog(this, actionText, "Confirm Action", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (cancelRental) {
                rentalService.cancelRental(rental.getRentalId());
                JOptionPane.showMessageDialog(this, "Rental #" + rental.getRentalId() + " cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            } else {
                rentalService.startRental(rental.getRentalId());
                JOptionPane.showMessageDialog(this, "Rental #" + rental.getRentalId() + " is now ONGOING.", "Started", JOptionPane.INFORMATION_MESSAGE);
            }
            loadPendingCancellations();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Action failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        java.awt.EventQueue.invokeLater(() -> new RentalServicePanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton A;
    private javax.swing.JButton Condition;
    private javax.swing.JButton Condition1;
    private javax.swing.JButton Condition2;
    private javax.swing.JButton Condition3;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnMerchInventory;
    private javax.swing.JButton btnPromos;
    private javax.swing.JButton btnRentalService;
    private javax.swing.JButton btnRenterName;
    private javax.swing.JButton btnRenterName1;
    private javax.swing.JButton btnRenterName10;
    private javax.swing.JButton btnRenterName2;
    private javax.swing.JButton btnRenterName3;
    private javax.swing.JButton btnRenterName4;
    private javax.swing.JButton btnRenterName5;
    private javax.swing.JButton btnRenterName6;
    private javax.swing.JButton btnRenterName7;
    private javax.swing.JButton btnRenterName8;
    private javax.swing.JButton btnRenterName9;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JComboBox<String> jComboBox7;
    private javax.swing.JComboBox<String> jComboBox8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JLabel lbOrderId;
    private javax.swing.JLabel lbOrderId1;
    private javax.swing.JLabel lbOrderId2;
    private javax.swing.JLabel lbOrderId3;
    private javax.swing.JLabel lbOrderId4;
    private javax.swing.JLabel lbOrderId5;
    private javax.swing.JLabel lbOrderId6;
    private javax.swing.JLabel lbOrderId7;
    private javax.swing.JLabel lbOrderId8;
    private javax.swing.JLabel lbOrderId9;
    private javax.swing.JLabel lblPromos;
    private javax.swing.JLabel pnlPreOrder;
    private javax.swing.JLabel pnlPreOrder1;
    private javax.swing.JTextField txtContactNum;
    private javax.swing.JTextField txtContactNum1;
    private javax.swing.JTextField txtContactNum2;
    private javax.swing.JTextField txtContactNum3;
    private javax.swing.JTextField txtContactNum4;
    private javax.swing.JTextField txtEventVenue;
    private javax.swing.JTextField txtRenterAddress;
    private javax.swing.JTextField txtRenterName;
    // End of variables declaration//GEN-END:variables
}
