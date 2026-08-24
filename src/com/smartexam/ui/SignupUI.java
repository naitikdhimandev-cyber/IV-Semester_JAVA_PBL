package com.smartexam.ui;

import com.smartexam.db.UserDAO;
import com.smartexam.util.HashUtil;

import javax.swing.*;
import java.awt.*;

public class SignupUI extends JFrame {

    public SignupUI() {
        setTitle("Smart Exam - Sign Up");
        setSize(320, 240);
        setLayout(new GridLayout(4, 2));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        String[] roles = { "student", "teacher" };
        JComboBox<String> roleDropdown = new JComboBox<>(roles);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        add(new JLabel(" Username:"));
        add(userField);
        add(new JLabel(" Password:"));
        add(passField);
        add(new JLabel(" Role:"));
        add(roleDropdown);
        add(backBtn);
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String role = (String) roleDropdown.getSelectedItem();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty!");
                return;
            }

            if (UserDAO.userExists(user)) {
                JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.");
                return;
            }

            String hashedPass = HashUtil.md5(pass);
            boolean success = UserDAO.createUser(user, hashedPass, role);

            if (success) {
                JOptionPane.showMessageDialog(this, "Account created successfully!\nYou can now log in.");
                dispose();
                new MainDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create account. Try again.");
            }
        });

        backBtn.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        setVisible(true);
    }
}
