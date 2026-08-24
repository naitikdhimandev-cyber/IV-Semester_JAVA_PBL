package com.smartexam.ui;

import com.smartexam.db.AssignmentDAO;
import com.smartexam.db.CertificateDAO;
import com.smartexam.db.ResultDAO;
import com.smartexam.db.TestDAO;
import com.smartexam.ai.CheatingDetector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final String username;
    private final int studentId;

    private final DefaultListModel<String> testsModel = new DefaultListModel<>();
    private final DefaultListModel<String> resultsModel = new DefaultListModel<>();
    private final DefaultListModel<String> certificatesModel = new DefaultListModel<>();
    private final List<CertificateDAO.CertificateRecord> certificateRecords = new ArrayList<>();

    public StudentDashboard(String username, int studentId) {
        this.username = username;
        this.studentId = studentId;

        setTitle("Student Dashboard - " + username);
        setSize(600, 350);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JList<String> testsList = new JList<>(testsModel);
        JList<String> resultsList = new JList<>(resultsModel);
        JList<String> certificatesList = new JList<>(certificatesModel);

        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel testsPanel = new JPanel(new BorderLayout());
        testsPanel.add(new JLabel("Assigned tests (pending — one attempt each)"), BorderLayout.NORTH);
        testsPanel.add(new JScrollPane(testsList), BorderLayout.CENTER);

        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.add(new JLabel("Result History"), BorderLayout.NORTH);
        resultsPanel.add(new JScrollPane(resultsList), BorderLayout.CENTER);

        listsPanel.add(testsPanel);
        listsPanel.add(resultsPanel);

        JPanel certificatesTab = new JPanel(new BorderLayout(8, 8));
        certificatesTab.add(new JLabel("Certificates (double-click to view)"), BorderLayout.NORTH);
        certificatesTab.add(new JScrollPane(certificatesList), BorderLayout.CENTER);
        JPanel certButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton verifyCertBtn = new JButton("Verify Certificate");
        certButtons.add(verifyCertBtn);
        certificatesTab.add(certButtons, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        JPanel testsResultsPanel = new JPanel(new BorderLayout());
        testsResultsPanel.add(listsPanel, BorderLayout.CENTER);
        tabs.addTab("Tests & Results", testsResultsPanel);
        tabs.addTab("My Certificates", certificatesTab);

        JButton startExamBtn = new JButton("Start Selected Test");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(startExamBtn);
        bottomPanel.add(refreshBtn);
        bottomPanel.add(logoutBtn);

        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        verifyCertBtn.addActionListener(e -> new CertificateVerifyUI());

        // Actions
        startExamBtn.addActionListener(e -> {
            String selectedTest = testsList.getSelectedValue();
            if (selectedTest == null) {
                JOptionPane.showMessageDialog(this, "Please select a test.");
                return;
            }
            int testId = TestDAO.getTestIdByName(selectedTest);
            if (testId <= 0) {
                JOptionPane.showMessageDialog(this, "Could not find selected test.");
                return;
            }
            if (ResultDAO.hasAttemptedTest(studentId, testId)) {
                JOptionPane.showMessageDialog(this,
                        "You have already completed this test.\nOnly one attempt is allowed.");
                return;
            }
            if (CheatingDetector.checkBeforeExam()) {
                dispose();
                new ExamUI(studentId, testId, selectedTest);
            }
        });

        refreshBtn.addActionListener(e -> loadData());

        logoutBtn.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        resultsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = resultsList.getSelectedValue();
                    if (selected != null) {
                        JOptionPane.showMessageDialog(StudentDashboard.this, selected, "Result Details",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        certificatesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int idx = certificatesList.getSelectedIndex();
                    if (idx >= 0 && idx < certificateRecords.size()) {
                        showCertificateDialog(certificateRecords.get(idx));
                    }
                }
            }
        });

        loadData();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showCertificateDialog(CertificateDAO.CertificateRecord r) {
        boolean integrity = CertificateDAO.verifyIntegrity(r);
        boolean chain = CertificateDAO.previousBlockExists(r.prevHash);
        JTextArea area = new JTextArea(CertificateViewHelper.formatCertificateText(r, integrity, chain));
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(480, 320));
        JOptionPane.showMessageDialog(this, sp, "Certificate", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadData() {
        testsModel.clear();
        List<String> tests = AssignmentDAO.getAssignedTestsForStudent(studentId);
        for (String t : tests) {
            int tid = TestDAO.getTestIdByName(t);
            if (tid > 0 && !ResultDAO.hasAttemptedTest(studentId, tid)) {
                testsModel.addElement(t);
            }
        }

        resultsModel.clear();
        List<String> results = ResultDAO.getResultsForStudent(studentId);
        for (String r : results) {
            resultsModel.addElement(r);
        }

        certificatesModel.clear();
        certificateRecords.clear();
        certificateRecords.addAll(CertificateDAO.listForStudent(studentId));
        for (CertificateDAO.CertificateRecord c : certificateRecords) {
            certificatesModel.addElement(
                    c.testName + " — " + c.score + "/" + c.totalQuestions + " — ID: " + c.certificateId);
        }
    }
}
