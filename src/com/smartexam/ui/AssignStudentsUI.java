package com.smartexam.ui;

import com.smartexam.db.AssignmentDAO;
import com.smartexam.db.TestDAO;
import com.smartexam.db.UserDAO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AssignStudentsUI extends JFrame {

    private final int teacherId;

    public AssignStudentsUI(int teacherId) {
        this.teacherId = teacherId;

        setTitle("Assign Tests to Students");
        setSize(400, 300);
        setLayout(new BorderLayout());

        List<String> tests = TestDAO.getAllTests();
        JComboBox<String> testCombo = new JComboBox<>(tests.toArray(new String[0]));

        DefaultListModel<String> studentModel = new DefaultListModel<>();
        for (String s : UserDAO.getAllStudentUsernames()) {
            studentModel.addElement(s);
        }
        JList<String> studentList = new JList<>(studentModel);
        studentList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JButton assignBtn = new JButton("Assign");

        assignBtn.addActionListener(e -> {
            String selectedTest = (String) testCombo.getSelectedItem();
            if (selectedTest == null) {
                JOptionPane.showMessageDialog(this, "Select a test.");
                return;
            }
            int testId = TestDAO.getTestIdByName(selectedTest);
            for (String student : studentList.getSelectedValuesList()) {
                int studentId = UserDAO.getUserIdByUsername(student);
                if (studentId > 0) {
                    AssignmentDAO.assignTestToStudent(testId, studentId);
                }
            }
            JOptionPane.showMessageDialog(this, "Assigned.");
        });

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Test:"));
        topPanel.add(testCombo);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(studentList), BorderLayout.CENTER);
        add(assignBtn, BorderLayout.SOUTH);

        setVisible(true);
    }
}
