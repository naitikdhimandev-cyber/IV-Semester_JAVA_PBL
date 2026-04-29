package com.smartexam.ui;

import com.smartexam.db.QuestionDAO;
import com.smartexam.db.ResultDAO;
import com.smartexam.db.TestDAO;
import com.smartexam.db.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class TeacherDashboard extends JFrame {

    private final String username;
    private final int teacherId;

    public TeacherDashboard(String username, int teacherId) {
        this.username = username;
        this.teacherId = teacherId;

        setTitle("Teacher Dashboard - " + username);
        setSize(700, 400);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left: list of students
        DefaultListModel<String> studentModel = new DefaultListModel<>();
        List<String> students = UserDAO.getAllStudentUsernames();
        for (String s : students) {
            studentModel.addElement(s);
        }
        JList<String> studentList = new JList<>(studentModel);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Students"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(studentList), BorderLayout.CENTER);

        // Center/right: test tools and views
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Top: test selection and actions
        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> testCombo = new JComboBox<>(TestDAO.getAllTests().toArray(new String[0]));
        JButton viewQuestionsBtn = new JButton("View Questions");
        JButton viewResultsBtn = new JButton("View Results");
        JButton createTestBtn = new JButton("Create Test");
        JButton assignTestBtn = new JButton("Assign Test");
        JButton verifyCertBtn = new JButton("Verify Certificate");
        JButton violationsBtn = new JButton("Review Violations");
        JButton logoutBtn = new JButton("Logout");

        topRight.add(new JLabel("Test:"));
        topRight.add(testCombo);
        topRight.add(viewQuestionsBtn);
        topRight.add(viewResultsBtn);
        topRight.add(createTestBtn);
        topRight.add(assignTestBtn);
        topRight.add(verifyCertBtn);
        topRight.add(violationsBtn);
        topRight.add(logoutBtn);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        JScrollPane infoScroll = new JScrollPane(infoArea);

        rightPanel.add(topRight, BorderLayout.NORTH);
        rightPanel.add(infoScroll, BorderLayout.CENTER);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Button actions
        createTestBtn.addActionListener(e -> new CreateTestUI(teacherId));
        assignTestBtn.addActionListener(e -> new AssignStudentsUI(teacherId));

        verifyCertBtn.addActionListener(e -> new CertificateVerifyUI());
        violationsBtn.addActionListener(e -> new ViolationsReportUI());

        logoutBtn.addActionListener(e -> {
            dispose();
            new MainDashboard();
        });

        viewQuestionsBtn.addActionListener(e -> {
            String selectedTest = (String) testCombo.getSelectedItem();
            if (selectedTest == null) {
                JOptionPane.showMessageDialog(this, "Please select a test.");
                return;
            }
            int testId = TestDAO.getTestIdByName(selectedTest);
            List<QuestionDAO.QuestionRecord> qs = QuestionDAO.getQuestionsByTestId(testId);
            StringBuilder sb = new StringBuilder();
            sb.append("Questions for ").append(selectedTest).append(":\n\n");
            int i = 1;
            for (QuestionDAO.QuestionRecord q : qs) {
                sb.append(i++).append(") ").append(q.question).append("\n");
                sb.append("   A) ").append(q.optionA).append("\n");
                sb.append("   B) ").append(q.optionB).append("\n");
                sb.append("   C) ").append(q.optionC).append("\n");
                sb.append("   D) ").append(q.optionD).append("\n");
                sb.append("   Correct: ").append(q.correctOption).append("\n\n");
            }
            if (qs.isEmpty()) {
                sb.append("No questions for this test yet.");
            }
            infoArea.setText(sb.toString());
        });

        viewResultsBtn.addActionListener(e -> {
            String selectedTest = (String) testCombo.getSelectedItem();
            if (selectedTest == null) {
                JOptionPane.showMessageDialog(this, "Please select a test.");
                return;
            }
            int testId = TestDAO.getTestIdByName(selectedTest);
            List<String> results = ResultDAO.getResultsForTest(testId);
            StringBuilder sb = new StringBuilder();
            sb.append("Results for ").append(selectedTest).append(":\n\n");
            for (String r : results) {
                sb.append(r).append("\n");
            }
            if (results.isEmpty()) {
                sb.append("No results for this test yet.");
            }
            infoArea.setText(sb.toString());
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
