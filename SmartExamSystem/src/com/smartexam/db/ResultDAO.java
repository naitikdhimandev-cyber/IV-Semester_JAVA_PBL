package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    /**
     * True if this student already has a submitted result for this test (one attempt only).
     */
    public static boolean hasAttemptedTest(int studentId, int testId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            String sql = "SELECT 1 FROM results WHERE student_id=? AND test_id=? LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, testId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int saveResult(int studentId, int testId, int score) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO results(student_id, test_id, score, timestamp) VALUES(?,?,?,datetime('now'))";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, studentId);
            ps.setInt(2, testId);
            ps.setInt(3, score);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> getResultsForStudent(int studentId) {
        List<String> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT t.test_name, r.score, r.timestamp FROM results r JOIN tests t ON r.test_id = t.id WHERE r.student_id=? ORDER BY r.timestamp DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String row = rs.getString("test_name") + " - Score: " + rs.getInt("score") + " - " + rs.getString("timestamp");
                results.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> getResultsForTest(int testId) {
        List<String> results = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.username, r.score, r.timestamp FROM results r JOIN users u ON r.student_id = u.id WHERE r.test_id=? ORDER BY r.timestamp DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String row = rs.getString("username") + " - Score: " + rs.getInt("score") + " - " + rs.getString("timestamp");
                results.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
