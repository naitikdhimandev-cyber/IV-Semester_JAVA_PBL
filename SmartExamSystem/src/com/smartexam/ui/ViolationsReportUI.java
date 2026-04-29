package com.smartexam.ui;

import com.smartexam.db.ViolationDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViolationsReportUI extends JFrame {

    private DefaultTableModel model;
    private JComboBox<String> studentFilter, testFilter, typeFilter;

    public ViolationsReportUI() {
        setTitle("Cheating Violations Report");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 250));

        // Header and Filters
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.setOpaque(false);

        JLabel header = new JLabel("Exam Security Violation Logs", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(new Color(40, 44, 52));
        topPanel.add(header);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);

        studentFilter = new JComboBox<>();
        testFilter = new JComboBox<>();
        typeFilter = new JComboBox<>();

        filterPanel.add(new JLabel("Student:"));
        filterPanel.add(studentFilter);
        filterPanel.add(new JLabel("Test:"));
        filterPanel.add(testFilter);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeFilter);

        JButton applyBtn = new JButton("Apply Filters");
        JButton resetBtn = new JButton("Reset");
        filterPanel.add(applyBtn);
        filterPanel.add(resetBtn);

        topPanel.add(filterPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        String[] cols = { "ID", "Student", "Exam", "Violation Type", "Details", "Timestamp" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scroll = new JScrollPane(table);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close Report");
        closeBtn.addActionListener(e -> dispose());
        mainPanel.add(closeBtn, BorderLayout.SOUTH);

        add(mainPanel);

        // Populate Filters and Initial Data
        List<ViolationDAO.ViolationRecord> all = ViolationDAO.getAllViolations();
        populateFilterCombos(all);
        loadData(all);

        applyBtn.addActionListener(e -> loadData(ViolationDAO.getAllViolations()));
        resetBtn.addActionListener(e -> {
            studentFilter.setSelectedIndex(0);
            testFilter.setSelectedIndex(0);
            typeFilter.setSelectedIndex(0);
            loadData(ViolationDAO.getAllViolations());
        });

        setVisible(true);
    }

    private void populateFilterCombos(List<ViolationDAO.ViolationRecord> list) {
        studentFilter.addItem("All Students");
        testFilter.addItem("All Tests");
        typeFilter.addItem("All Types");

        java.util.Set<String> students = new java.util.HashSet<>();
        java.util.Set<String> tests = new java.util.HashSet<>();
        java.util.Set<String> types = new java.util.HashSet<>();

        for (ViolationDAO.ViolationRecord v : list) {
            students.add(v.username);
            tests.add(v.testName);
            types.add(v.type);
        }

        students.stream().sorted().forEach(studentFilter::addItem);
        tests.stream().sorted().forEach(testFilter::addItem);
        types.stream().sorted().forEach(typeFilter::addItem);
    }

    private void loadData(List<ViolationDAO.ViolationRecord> list) {
        model.setRowCount(0);
        String sFilter = (String) studentFilter.getSelectedItem();
        String tFilter = (String) testFilter.getSelectedItem();
        String vFilter = (String) typeFilter.getSelectedItem();

        for (ViolationDAO.ViolationRecord v : list) {
            boolean matchS = sFilter == null || sFilter.equals("All Students") || v.username.equals(sFilter);
            boolean matchT = tFilter == null || tFilter.equals("All Tests") || v.testName.equals(tFilter);
            boolean matchV = vFilter == null || vFilter.equals("All Types") || v.type.equals(vFilter);

            if (matchS && matchT && matchV) {
                model.addRow(new Object[] { v.id, v.username, v.testName, v.type, v.details, v.timestamp });
            }
        }
    }
}
