package com.smartexam.ui;

import com.smartexam.db.CertificateDAO;

import javax.swing.*;
import java.awt.*;

public final class CertificateViewHelper {

    private CertificateViewHelper() {
    }

    public static String formatCertificateText(CertificateDAO.CertificateRecord r, boolean verified,
                                               boolean chainLinked) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════╗\n");
        sb.append("║           SMART EXAM — DIGITAL CERTIFICATE               ║\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n\n");
        sb.append("Certificate ID (blockchain hash): ").append(r.certificateId).append("\n\n");
        sb.append("Student: ").append(r.studentName).append("  (user id: ").append(r.studentId).append(")\n");
        sb.append("Test: ").append(r.testName).append("  (test id: ").append(r.testId).append(")\n");
        sb.append("Score: ").append(r.score).append(" / ").append(r.totalQuestions).append("\n");
        sb.append("Issued: ").append(r.issuedAt).append("\n");
        sb.append("Result record id: ").append(r.resultId).append("\n\n");
        sb.append("Chain integrity:\n");
        sb.append("  Previous block hash: ").append(r.prevHash).append("\n");
        sb.append("  This block hash:     ").append(r.blockHash).append("\n\n");
        sb.append("Cryptographic verification: ").append(verified ? "PASS" : "FAIL").append("\n");
        sb.append("Chain link verification: ").append(chainLinked ? "PASS" : "FAIL").append("\n");
        return sb.toString();
    }

    public static JPanel createStatusPanel(boolean verified, boolean chainLinked) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel l = new JLabel();
        if (verified && chainLinked) {
            l.setText("Status: VERIFIED — certificate is authentic and untampered.");
            l.setForeground(new Color(0, 120, 0));
        } else {
            l.setText("Status: NOT VERIFIED — data may be invalid or tampered.");
            l.setForeground(new Color(180, 0, 0));
        }
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        p.add(l);
        return p;
    }
}
