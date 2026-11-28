package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.dao.ProductDAO;
import com.mycompany.oop.core.finals.dao.SaleDAO;
import com.mycompany.oop.core.finals.model.Product;
import com.mycompany.oop.core.finals.model.Rental;
import com.mycompany.oop.core.finals.model.RentalStatus;
import com.mycompany.oop.core.finals.model.Sale;
import com.mycompany.oop.core.finals.model.StockStatus;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.RentalService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author Frich Ramos
 */
public class DashboardPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DashboardPanel.class.getName());
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy");

    private UserSession session;
    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();
    private final RentalService rentalService = new RentalService();

    private YearMonth currentMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();
    private List<Rental> cachedRentals = new ArrayList<>();
    private List<Product> cachedProducts = new ArrayList<>();
    private DefaultListModel<String> activityListModel;

    /**
     * Creates new form DashboardPanel
     */
    public DashboardPanel() {
        this(null);
    }

    public DashboardPanel(UserSession session) {
        this.session = session;
        initComponents();
        setLocationRelativeTo(null);
        setupNavigation();
        setupDashboardWidgets();
        loadDashboardData();
    }

    private void setupNavigation() {
        jButton3.addActionListener(e -> {
            new MerchInventoryPanel(session).setVisible(true);
            this.dispose();
        });
        jButton7.addActionListener(e -> {
            new RentalServicePanel(session).setVisible(true);
            this.dispose();
        });
        MouseAdapter promoNav = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PromosPanel(session).setVisible(true);
                dispose();
            }
        };
        jPanel4.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jLabel6.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jPanel4.addMouseListener(promoNav);
        jLabel6.addMouseListener(promoNav);

        jButton5.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                session != null && session.isLoggedIn()
                        ? "Logged in as " + session.getCurrentUser().getFullName() + " (" + session.getCurrentUser().getRole() + ")"
                        : "Not logged in.",
                "Settings",
                JOptionPane.INFORMATION_MESSAGE));
    }

    private void setupDashboardWidgets() {
        if (session != null && session.isLoggedIn()) {
            String fullName = session.getCurrentUser().getFullName();
            jLabel2.setText("Welcome back, " + fullName + "!");
            jLabel5.setText(fullName);
            jLabel4.setText(session.getCurrentUser().getRole().name());
        }

        // 1. Make Stock Status card (jPanel15) scrollable with live table
        jPanel15.removeAll();
        jPanel15.setLayout(new BorderLayout(0, 8));
        jPanel15.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel lblStockHeader = new JLabel("Stock Status");
        lblStockHeader.setFont(new Font("Yu Gothic UI", Font.BOLD, 15));
        lblStockHeader.setForeground(new Color(245, 245, 245));
        jPanel15.add(lblStockHeader, BorderLayout.NORTH);

        DefaultTableModel stockModel = new DefaultTableModel(new String[]{"Product", "Qty", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblStock = new JTable(stockModel);
        tblStock.setBackground(new Color(18, 56, 104));
        tblStock.setForeground(new Color(245, 245, 245));
        tblStock.setGridColor(new Color(33, 76, 130));
        tblStock.setRowHeight(26);
        tblStock.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        tblStock.getTableHeader().setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblStock.getColumnModel().getColumn(1).setMaxWidth(80);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblStock.getColumnModel().getColumn(2).setMaxWidth(130);
        tblStock.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String s = value != null ? value.toString() : "";
                if ("OUT OF STOCK".equals(s)) {
                    c.setForeground(new Color(255, 110, 110));
                } else if ("LOW STOCK".equals(s)) {
                    c.setForeground(new Color(255, 195, 77));
                } else {
                    c.setForeground(new Color(110, 231, 155));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        tblStock.setName("tblDashboardStock");

        JScrollPane stockScroll = new JScrollPane(tblStock);
        stockScroll.setBorder(BorderFactory.createLineBorder(new Color(33, 76, 130)));
        stockScroll.getViewport().setBackground(new Color(18, 56, 104));
        jPanel15.add(stockScroll, BorderLayout.CENTER);
        jPanel15.revalidate();
        jPanel15.repaint();

        // 2. Make Calendar (jPanel6 + jPanel9) interactive with < and > month controls
        JButton btnPrevMonth = new JButton("<");
        JButton btnNextMonth = new JButton(">");
        for (JButton b : new JButton[]{btnPrevMonth, btnNextMonth}) {
            b.setMargin(new Insets(1, 6, 1, 6));
            b.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
            b.setFocusPainted(false);
        }
        btnPrevMonth.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendar();
        });
        btnNextMonth.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendar();
        });
        jPanel6.add(btnPrevMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 35, 26));
        jPanel6.add(btnNextMonth, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 10, 35, 26));
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 10, 110, 26));
        jLabel9.setHorizontalAlignment(SwingConstants.CENTER);

        jPanel9.removeAll();
        jPanel9.setLayout(new GridLayout(6, 7, 4, 4));
        jPanel9.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // 3. Make Recent Activities / Selected Date & Stock Status (jPanel8) scrollable
        jPanel8.removeAll();
        jPanel8.setLayout(new BorderLayout(0, 8));
        jPanel8.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        jLabel8.setText("Date Schedule & Stock Status");
        jPanel8.add(jLabel8, BorderLayout.NORTH);

        activityListModel = new DefaultListModel<>();
        JList<String> activityList = new JList<>(activityListModel);
        activityList.setBackground(new Color(13, 47, 90));
        activityList.setForeground(new Color(240, 244, 248));
        activityList.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        activityList.setFixedCellHeight(28);
        JScrollPane actScroll = new JScrollPane(activityList);
        actScroll.setBorder(BorderFactory.createLineBorder(new Color(33, 76, 130)));
        jPanel8.add(actScroll, BorderLayout.CENTER);
        jPanel8.revalidate();
        jPanel8.repaint();
    }

    private void loadDashboardData() {
        // Load Total Sales
        try {
            List<Sale> sales = saleDAO.getAllSales();
            long totalCentavos = 0;
            for (Sale s : sales) {
                totalCentavos += s.getTotalCentavos();
            }
            jLabel21.setText("PHP");
            jLabel21.setFont(new Font("Yu Gothic UI", Font.BOLD, 14));
            jLabel23.setText(String.format("%,.2f", totalCentavos / 100.0));
            jLabel23.setFont(new Font("Yu Gothic UI", Font.BOLD, 34));
            jLabel20.setText(YearMonth.now().format(MONTH_FMT));
        } catch (SQLException ex) {
            logger.warning("Could not load sales for dashboard: " + ex.getMessage());
        }

        // Load Stock Status
        try {
            cachedProducts = productDAO.getAllProducts();
            for (Component comp : jPanel15.getComponents()) {
                if (comp instanceof JScrollPane sp && sp.getViewport().getView() instanceof JTable tbl) {
                    DefaultTableModel model = (DefaultTableModel) tbl.getModel();
                    model.setRowCount(0);
                    for (Product p : cachedProducts) {
                        StockStatus st = computeStockStatus(p);
                        String statusLabel = switch (st) {
                            case OUT_OF_STOCK -> "OUT OF STOCK";
                            case LOW_STOCK -> "LOW STOCK";
                            case IN_STOCK -> "IN STOCK";
                        };
                        model.addRow(new Object[]{p.getProductName(), p.getQuantity(), statusLabel});
                    }
                }
            }
        } catch (SQLException ex) {
            logger.warning("Could not load products for dashboard: " + ex.getMessage());
        }

        // Load Rentals for Calendar
        try {
            cachedRentals = rentalService.getAllRentals();
        } catch (SQLException ex) {
            cachedRentals = new ArrayList<>();
            logger.warning("Could not load rentals for calendar: " + ex.getMessage());
        }

        renderCalendar();
        updateSelectedDateDetails(selectedDate);
    }

    private void renderCalendar() {
        jLabel9.setText(currentMonth.format(MONTH_FMT));
        jPanel9.removeAll();

        LocalDate firstDay = currentMonth.atDay(1);
        int startOffset = firstDay.getDayOfWeek().getValue() % 7; // Sun=0, Mon=1, ..., Sat=6
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int cell = 0; cell < 42; cell++) {
            int dayNum = cell - startOffset + 1;
            if (dayNum < 1 || dayNum > daysInMonth) {
                JLabel blank = new JLabel("");
                jPanel9.add(blank);
                continue;
            }
            LocalDate date = currentMonth.atDay(dayNum);
            boolean hasRental = hasRentalOnDate(date);
            boolean isToday = date.equals(today);
            boolean isSelected = date.equals(selectedDate);

            JButton dayBtn = new JButton(String.valueOf(dayNum));
            dayBtn.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
            dayBtn.setMargin(new Insets(0, 0, 0, 0));
            dayBtn.setFocusPainted(false);
            dayBtn.setForeground(Color.WHITE);
            dayBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (isSelected) {
                dayBtn.setBackground(new Color(0, 140, 210));
                dayBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            } else if (hasRental) {
                dayBtn.setBackground(new Color(22, 115, 65));
                dayBtn.setToolTipText("Has scheduled rental(s)");
            } else if (isToday) {
                dayBtn.setBackground(new Color(35, 95, 165));
            } else {
                dayBtn.setBackground(new Color(18, 58, 108));
            }

            dayBtn.addActionListener(e -> {
                selectedDate = date;
                renderCalendar();
                updateSelectedDateDetails(date);
            });
            jPanel9.add(dayBtn);
        }
        jPanel9.revalidate();
        jPanel9.repaint();
    }

    private boolean hasRentalOnDate(LocalDate date) {
        for (Rental r : cachedRentals) {
            if (r.getStatus() == RentalStatus.CANCELLED) continue;
            if (r.getRentalStart() == null || r.getExpectedReturn() == null) continue;
            LocalDate start = r.getRentalStart().toLocalDate();
            LocalDate end = r.getExpectedReturn().toLocalDate();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    private void updateSelectedDateDetails(LocalDate date) {
        if (activityListModel == null) return;
        activityListModel.clear();
        activityListModel.addElement("Selected Date: " + date.toString());

        int rentalCount = 0;
        for (Rental r : cachedRentals) {
            if (r.getStatus() == RentalStatus.CANCELLED) continue;
            if (r.getRentalStart() == null || r.getExpectedReturn() == null) continue;
            LocalDate start = r.getRentalStart().toLocalDate();
            LocalDate end = r.getExpectedReturn().toLocalDate();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                rentalCount++;
                activityListModel.addElement("  [RENTAL #" + r.getRentalId() + "] "
                        + r.getCustomerName() + " (" + r.getStatus() + ") - " + r.getVenue());
            }
        }
        if (rentalCount == 0) {
            activityListModel.addElement("  No rentals scheduled on this date.");
        }

        activityListModel.addElement("── Stock Status Summary ──");
        if (cachedProducts.isEmpty()) {
            activityListModel.addElement("  No active products loaded.");
        } else {
            for (Product p : cachedProducts) {
                String tag = switch (computeStockStatus(p)) {
                    case OUT_OF_STOCK -> "[OUT OF STOCK]";
                    case LOW_STOCK -> "[LOW STOCK]";
                    case IN_STOCK -> "[IN STOCK]";
                };
                activityListModel.addElement("  " + tag + " " + p.getProductName() + " — Qty: " + p.getQuantity());
            }
        }
    }

    private StockStatus computeStockStatus(Product p) {
        if (p.getQuantity() <= 0) {
            return StockStatus.OUT_OF_STOCK;
        }
        if (p.getQuantity() <= p.getLowStockThreshold()) {
            return StockStatus.LOW_STOCK;
        }
        return StockStatus.IN_STOCK;
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
            this, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (session != null) session.endSession();
            new LoginFrame().setVisible(true);
            this.dispose();
        }
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
