package com.smartexam.ui;

import com.smartexam.db.CertificateDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CertificateVerifyUI extends JFrame {

    public CertificateVerifyUI() {
        setTitle("Verify Certificate");
        setSize(560, 480);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(new EmptyBorder(10, 10, 0, 10));
        JTextField idField = new JTextField();
        JButton verifyBtn = new JButton("Verify");
        top.add(new JLabel("Certificate ID:"), BorderLayout.NORTH);
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.add(idField, BorderLayout.CENTER);
        row.add(verifyBtn, BorderLayout.EAST);
        top.add(row, BorderLayout.CENTER);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel statusWrap = new JPanel(new BorderLayout());
        statusWrap.setBorder(new EmptyBorder(0, 10, 10, 10));

        verifyBtn.addActionListener(e -> {
            String raw = idField.getText();
            if (raw == null || raw.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter a certificate ID.");
                return;
            }
            CertificateDAO.CertificateRecord r = CertificateDAO.findByCertificateId(raw.trim());
            if (r == null) {
                statusWrap.removeAll();
                statusWrap.add(CertificateViewHelper.createStatusPanel(false, false), BorderLayout.CENTER);
                area.setText("No certificate found for this ID.");
                statusWrap.revalidate();
                statusWrap.repaint();
                return;
            }
            boolean integrity = CertificateDAO.verifyIntegrity(r);
            boolean chain = CertificateDAO.previousBlockExists(r.prevHash);
            statusWrap.removeAll();
            statusWrap.add(CertificateViewHelper.createStatusPanel(integrity, chain), BorderLayout.CENTER);
            area.setText(CertificateViewHelper.formatCertificateText(r, integrity, chain));
            statusWrap.revalidate();
            statusWrap.repaint();
        });

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(statusWrap, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
