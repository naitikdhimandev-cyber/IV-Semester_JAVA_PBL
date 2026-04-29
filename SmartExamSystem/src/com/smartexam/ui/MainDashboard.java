
package com.smartexam.ui;

import javax.swing.*;
import java.awt.*;

public class MainDashboard extends JFrame {

    public MainDashboard() {
        setTitle("Smart Exam - Choose Role");
        setSize(300, 200);
        setLayout(new FlowLayout());

        JButton studentBtn = new JButton("Login as Student");
        JButton teacherBtn = new JButton("Login as Teacher");
        JButton signupBtn = new JButton("Sign Up");

        studentBtn.addActionListener(e -> {
            dispose();
            new LoginUI("student");
        });

        teacherBtn.addActionListener(e -> {
            dispose();
            new LoginUI("teacher");
        });

        signupBtn.addActionListener(e -> {
            dispose();
            new SignupUI();
        });

        add(studentBtn);
        add(teacherBtn);
        add(signupBtn);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
