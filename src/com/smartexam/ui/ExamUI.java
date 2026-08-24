package com.smartexam.ui;

import com.smartexam.blockchain.CertificateChainService;
import com.smartexam.db.CertificateDAO;
import com.smartexam.db.QuestionDAO;
import com.smartexam.db.ResultDAO;
import com.smartexam.db.UserDAO;

import javax.swing.*;
import java.awt.*;
import com.smartexam.ai.MonitoringService;
import com.smartexam.db.ViolationDAO;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

public class ExamUI extends JFrame {

    private final int studentId;
    private final int testId;
    private final String testName;

    private List<QuestionDAO.QuestionRecord> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private final JLabel questionLabel = new JLabel();
    private final JRadioButton optionA = new JRadioButton();
    private final JRadioButton optionB = new JRadioButton();
    private final JRadioButton optionC = new JRadioButton();
    private final JRadioButton optionD = new JRadioButton();
    private final ButtonGroup group = new ButtonGroup();
    private final JButton nextButton = new JButton("Next");
    private final JLabel timerLabel = new JLabel();

    private javax.swing.Timer timer;
    private int secondsLeft = 60; // simple 1-minute timer for demo

    private final MonitoringService monitoringService;

    public ExamUI(int studentId, int testId, String testName) {
        this.studentId = studentId;
        this.testId = testId;
        this.testName = testName;
        this.monitoringService = new MonitoringService(studentId, testId);

        // Continuous Focus Monitoring
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if (timer != null && timer.isRunning()) {
                    // 2-second delay to ignore temporary focus loss (like system notifications)
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                        }
                        SwingUtilities.invokeLater(() -> {
                            java.awt.Window activeWindow = javax.swing.FocusManager.getCurrentManager()
                                    .getActiveWindow();
                            // If the window is still unfocused and the active window is NOT part of this
                            // app (external window)
                            if (!isFocused() && activeWindow == null && timer.isRunning()) {
                                System.out.println("ExamUI: CONFIRMED WINDOW FOCUS LOST TO EXTERNAL APP!");
                                ViolationDAO.logViolation(studentId, testId, "FOCUS_LOST",
                                        "Student switched windows during exam.");
                                JOptionPane.showMessageDialog(ExamUI.this,
                                        "Warning: Switching windows is not allowed!\nThis incident has been logged.",
                                        "Security Violation", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }).start();
                }
            }
        });

        monitoringService.start();

        if (ResultDAO.hasAttemptedTest(studentId, testId)) {
            JOptionPane.showMessageDialog(null,
                    "You have already completed this test.\nOnly one attempt is allowed.");
            String u = UserDAO.getUsernameById(studentId);
            new StudentDashboard(u.isEmpty() ? "student" : u, studentId);
            dispose();
            return;
        }

        questions = QuestionDAO.getQuestionsByTestId(testId);
        if (questions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No questions found for this test.");
            dispose();
            return;
        }

        setTitle("Exam - " + testName);
        setSize(500, 300);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(questionLabel, BorderLayout.CENTER);
        topPanel.add(timerLabel, BorderLayout.EAST);

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1));
        group.add(optionA);
        group.add(optionB);
        group.add(optionC);
        group.add(optionD);
        optionsPanel.add(optionA);
        optionsPanel.add(optionB);
        optionsPanel.add(optionC);
        optionsPanel.add(optionD);

        nextButton.addActionListener(e -> handleNext());

        add(topPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.CENTER);
        add(nextButton, BorderLayout.SOUTH);

        startTimer();
        showQuestion();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void startTimer() {
        timerLabel.setText("Time: " + secondsLeft + "s");
        timer = new javax.swing.Timer(1000, e -> {
            secondsLeft--;
            timerLabel.setText("Time: " + secondsLeft + "s");
            if (secondsLeft <= 0) {
                timer.stop();
                finishExam();
            }
        });
        timer.start();
    }

    private void showQuestion() {
        QuestionDAO.QuestionRecord q = questions.get(currentIndex);
        questionLabel.setText((currentIndex + 1) + ". " + q.question);
        optionA.setText("A) " + q.optionA);
        optionB.setText("B) " + q.optionB);
        optionC.setText("C) " + q.optionC);
        optionD.setText("D) " + q.optionD);
        group.clearSelection();

        if (currentIndex == questions.size() - 1) {
            nextButton.setText("Submit");
        } else {
            nextButton.setText("Next");
        }
    }

    private void handleNext() {
        String selected = null;
        if (optionA.isSelected())
            selected = "A";
        else if (optionB.isSelected())
            selected = "B";
        else if (optionC.isSelected())
            selected = "C";
        else if (optionD.isSelected())
            selected = "D";

        QuestionDAO.QuestionRecord q = questions.get(currentIndex);
        if (selected != null && selected.equalsIgnoreCase(q.correctOption)) {
            score++;
        }

        currentIndex++;
        if (currentIndex >= questions.size()) {
            if (timer != null) {
                timer.stop();
            }
            finishExam();
        } else {
            showQuestion();
        }
    }

    private void finishExam() {
        int totalQuestions = questions.size();
        int resultId = ResultDAO.saveResult(studentId, testId, score);
        String username = UserDAO.getUsernameById(studentId);
        CertificateDAO.CertificateRecord cert = null;
        if (resultId > 0) {
            cert = CertificateChainService.issue(
                    studentId, username, testId, testName, score, totalQuestions, resultId);
        }
        monitoringService.stop();
        dispose();
        new ResultUI(score, cert, studentId, username);
    }
}
