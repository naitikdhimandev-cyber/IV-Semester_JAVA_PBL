package com.smartexam.ui;

import com.smartexam.db.CertificateDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResultUI extends JFrame {

    public ResultUI(int score, CertificateDAO.CertificateRecord cert, int studentId, String username) {
        setTitle("Exam Result");
        setSize(520, 420);
        setLayout(new BorderLayout(8, 8));

        JPanel north = new JPanel(new BorderLayout());
        north.setBorder(new EmptyBorder(10, 10, 5, 10));
        JLabel result = new JLabel("Your score: " + score, SwingConstants.CENTER);
        result.setFont(result.getFont().deriveFont(Font.BOLD, 16f));
        north.add(result, BorderLayout.CENTER);

        JTextArea certArea = new JTextArea();
        certArea.setEditable(false);
        certArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(certArea);
        scroll.setBorder(new EmptyBorder(5, 10, 5, 10));

        if (cert != null) {
            boolean integrity = CertificateDAO.verifyIntegrity(cert);
            boolean chain = CertificateDAO.previousBlockExists(cert.prevHash);
            certArea.setText(CertificateViewHelper.formatCertificateText(cert, integrity, chain));
        } else {
            certArea.setText("Certificate could not be generated (check database). Your score was still saved.");
        }

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Back to Dashboard");
        south.add(backBtn);
        south.setBorder(new EmptyBorder(0, 10, 10, 10));

        backBtn.addActionListener(e -> {
            dispose();
            new StudentDashboard(username, studentId);
        });

        add(north, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
