package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ViolationDAO {

    public static class ViolationRecord {
        public int id;
        public String username;
        public String testName;
        public String type;
        public String details;
        public String timestamp;

        public ViolationRecord(int id, String username, String testName, String type, String details,
                String timestamp) {
            this.id = id;
            this.username = username;
            this.testName = testName;
            this.type = type;
            this.details = details;
            this.timestamp = timestamp;
        }
    }

    public static void logViolation(int studentId, int testId, String type, String details) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO violations(student_id, test_id, violation_type, violation_details, timestamp) VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, testId);
            ps.setString(3, type);
            ps.setString(4, details);
            ps.setString(5, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<ViolationRecord> getAllViolations() {
        List<ViolationRecord> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT v.id, u.username, t.test_name, v.violation_type, v.violation_details, v.timestamp " +
                    "FROM violations v " +
                    "JOIN users u ON v.student_id = u.id " +
                    "JOIN tests t ON v.test_id = t.id " +
                    "ORDER BY v.timestamp DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ViolationRecord(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("test_name"),
                        rs.getString("violation_type"),
                        rs.getString("violation_details"),
                        rs.getString("timestamp")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
