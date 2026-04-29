package com.smartexam.ui;

import com.smartexam.auth.LoginService;
import com.smartexam.db.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {

    private final String role;

    public LoginUI(String role) {
        this.role = role;
        setTitle("Smart Exam Login - " + role);
        setSize(340, 240);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 6, 6));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton verifyCertBtn = new JButton("Verify Certificate");

        form.setBorder(new EmptyBorder(12, 12, 8, 12));
        form.add(new JLabel("Username:"));
        form.add(userField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel());
        form.add(loginBtn);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(verifyCertBtn);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        verifyCertBtn.addActionListener(e -> new CertificateVerifyUI());

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter username and password.");
                return;
            }

            String actualRole = LoginService.authenticate(user, pass);
            if (actualRole == null) {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.\n\n"
                                + "Default test accounts: student / 123  or  teacher / 123");
                return;
            }
            if (!actualRole.equalsIgnoreCase(this.role)) {
                String hint = "teacher".equalsIgnoreCase(actualRole)
                        ? "Use \"Login as Teacher\" on the main screen."
                        : "Use \"Login as Student\" on the main screen.";
                JOptionPane.showMessageDialog(this,
                        "This account is registered as: " + actualRole + ".\n" + hint);
                return;
            }
            int userId = UserDAO.getUserIdByUsername(user);
            if (userId <= 0) {
                JOptionPane.showMessageDialog(this, "Could not load user. Try again.");
                return;
            }
            dispose();
            if ("teacher".equalsIgnoreCase(role)) {
                new TeacherDashboard(user, userId);
            } else {
                new StudentDashboard(user, userId);
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
