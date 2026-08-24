package com.smartexam.ui;

import com.smartexam.db.QuestionDAO;
import com.smartexam.db.TestDAO;

import javax.swing.*;
import java.awt.*;

public class CreateTestUI extends JFrame {

    private final int teacherId;
    private int testId = -1;

    public CreateTestUI(int teacherId) {
        this.teacherId = teacherId;

        setTitle("Create Test");
        setSize(400, 300);
        setLayout(new GridLayout(8, 2));

        JTextField testNameField = new JTextField();
        JTextField questionField = new JTextField();
        JTextField aField = new JTextField();
        JTextField bField = new JTextField();
        JTextField cField = new JTextField();
        JTextField dField = new JTextField();
        JTextField correctField = new JTextField();

        JButton createTestBtn = new JButton("Create Test");
        JButton addQuestionBtn = new JButton("Add Question");

        add(new JLabel("Test Name:"));
        add(testNameField);
        add(new JLabel("Question:"));
        add(questionField);
        add(new JLabel("Option A:"));
        add(aField);
        add(new JLabel("Option B:"));
        add(bField);
        add(new JLabel("Option C:"));
        add(cField);
        add(new JLabel("Option D:"));
        add(dField);
        add(new JLabel("Correct Option (A/B/C/D):"));
        add(correctField);
        add(createTestBtn);
        add(addQuestionBtn);

        createTestBtn.addActionListener(e -> {
            if (testId == -1) {
                String name = testNameField.getText();
                if (name != null && !name.isEmpty()) {
                    testId = TestDAO.createTest(name, teacherId);
                    if (testId > 0) {
                        JOptionPane.showMessageDialog(this, "Test created. Now add questions.");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to create test.");
                    }
                }
            }
        });

        addQuestionBtn.addActionListener(e -> {
            if (testId <= 0) {
                JOptionPane.showMessageDialog(this, "Please create the test first.");
                return;
            }
            String q = questionField.getText();
            String a = aField.getText();
            String b = bField.getText();
            String c = cField.getText();
            String d = dField.getText();
            String correct = correctField.getText();
            if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || correct.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields.");
                return;
            }
            QuestionDAO.addQuestion(testId, q, a, b, c, d, correct.toUpperCase());
            JOptionPane.showMessageDialog(this, "Question added.");
            questionField.setText("");
            aField.setText("");
            bField.setText("");
            cField.setText("");
            dField.setText("");
            correctField.setText("");
        });

        setVisible(true);
    }
}
