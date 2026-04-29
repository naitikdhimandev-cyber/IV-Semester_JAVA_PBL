package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public static void addQuestion(int testId, String question, String a, String b, String c, String d, String correct) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO questions(test_id, question, option_a, option_b, option_c, option_d, correct_option) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            ps.setString(2, question);
            ps.setString(3, a);
            ps.setString(4, b);
            ps.setString(5, c);
            ps.setString(6, d);
            ps.setString(7, correct);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<QuestionRecord> getQuestionsByTestId(int testId) {
        List<QuestionRecord> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM questions WHERE test_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                QuestionRecord q = new QuestionRecord();
                q.id = rs.getInt("id");
                q.testId = rs.getInt("test_id");
                q.question = rs.getString("question");
                q.optionA = rs.getString("option_a");
                q.optionB = rs.getString("option_b");
                q.optionC = rs.getString("option_c");
                q.optionD = rs.getString("option_d");
                q.correctOption = rs.getString("correct_option");
                list.add(q);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class QuestionRecord {
        public int id;
        public int testId;
        public String question;
        public String optionA;
        public String optionB;
        public String optionC;
        public String optionD;
        public String correctOption;
    }
}
