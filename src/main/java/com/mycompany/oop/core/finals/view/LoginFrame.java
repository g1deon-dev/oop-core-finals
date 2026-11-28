package com.mycompany.oop.core.finals.view;

import com.mycompany.oop.core.finals.dao.UserDAO;
import com.mycompany.oop.core.finals.model.User;
import com.mycompany.oop.core.finals.model.UserRole;
import com.mycompany.oop.core.finals.security.UserSession;
import com.mycompany.oop.core.finals.service.AuthService;
import com.mycompany.oop.core.finals.util.PasswordUtil;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

/**
 * Login screen for CORE (supports CHAIRPERSON, BUSINESS_MANAGER, and COMMITTEE_STAFF).
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JCheckBox chkShowPassword = new JCheckBox("Show password");
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnLogin = new JButton("Log In");

    public LoginFrame() {
        setTitle("CORE — Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(null);
        ensureDefaultAccounts();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 245));

        // Top navy banner matching MerchInventoryPanel
        JPanel header = new JPanel(new BorderLayout(12, 4));
        header.setBackground(new Color(9, 30, 57));
        header.setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));

        URL logoUrl = getClass().getResource("/com/mycompany/oop/core/finals/view/logo.png");
        if (logoUrl != null) {
            JLabel lblLogo = new JLabel(new ImageIcon(logoUrl));
            header.add(lblLogo, BorderLayout.WEST);
        }

        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);
        GridBagConstraints tc = new GridBagConstraints();
        tc.gridx = 0;
        tc.gridy = 0;
        tc.anchor = GridBagConstraints.WEST;

        JLabel lblTitle = new JLabel("CORE");
        lblTitle.setFont(new Font("Agency FB", Font.BOLD, 38));
        lblTitle.setForeground(new Color(245, 245, 245));
        titleBox.add(lblTitle, tc);

        tc.gridy = 1;
        JLabel lblSubtitle = new JLabel("Organization Inventory & Management");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(192, 210, 224));
        titleBox.add(lblSubtitle, tc);

        header.add(titleBox, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // Form body
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 245));
        form.setBorder(BorderFactory.createEmptyBorder(24, 36, 28, 36));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(30, 30, 30));
        gbc.gridy = 0;
        form.add(lblUser, gbc);

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 1;
        form.add(txtUsername, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(30, 30, 30));
        gbc.gridy = 2;
        gbc.insets = new Insets(12, 0, 6, 0);
        form.add(lblPass, gbc);

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(280, 36));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 4, 0);
        form.add(txtPassword, gbc);

        char defaultEcho = txtPassword.getEchoChar();
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.addActionListener(e ->
            txtPassword.setEchoChar(chkShowPassword.isSelected() ? (char) 0 : defaultEcho)
        );
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        form.add(chkShowPassword, gbc);

        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(200, 35, 35));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 5;
        gbc.insets = new Insets(2, 0, 8, 0);
        form.add(lblError, gbc);

        btnLogin.setBackground(new Color(11, 52, 90));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(280, 40));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 0, 0, 0);
        form.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> attemptLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> attemptLogin());

        root.add(form, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void attemptLogin() {
        lblError.setText(" ");
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        UserSession session = new UserSession();
        AuthService authService = new AuthService(session);

        try {
            User user = authService.login(username, password);
            DashboardPanel mainFrame = new DashboardPanel(session);
            mainFrame.setVisible(true);
            this.dispose();
        } catch (IllegalArgumentException ex) {
            setErrorText(ex.getMessage());
        } catch (Exception ex) {
            setErrorText("Login error: " + ex.getMessage());
        }
    }

    private void setErrorText(String message) {
        if (message == null || message.isBlank()) {
            lblError.setText(" ");
            return;
        }
        String escaped = message.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        lblError.setText("<html><div style='width:260px;text-align:center;'>" + escaped + "</div></html>");
        pack();
    }

    /**
     * Ensures starter accounts exist for all three roles if not already in the DB.
     */
    private void ensureDefaultAccounts() {
        new Thread(() -> {
            try {
                UserDAO userDAO = new UserDAO();
                seedIfMissing(userDAO, "admin", "Chairperson Admin", "admin1234", UserRole.CHAIRPERSON);
                seedIfMissing(userDAO, "manager", "Business Manager", "manager1234", UserRole.BUSINESS_MANAGER);
                seedIfMissing(userDAO, "staff", "Committee Staff", "staff1234", UserRole.COMMITTEE_STAFF);
            } catch (Exception ignored) {
                // If offline or DB unreachable, login will display the connection error.
            }
        }).start();
    }

    private void seedIfMissing(UserDAO userDAO, String username, String fullName,
                               String rawPassword, UserRole role) throws Exception {
        if (userDAO.findByUsername(username) == null) {
            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(rawPassword, salt);
            userDAO.createUser(username, fullName, hash, salt, role);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
