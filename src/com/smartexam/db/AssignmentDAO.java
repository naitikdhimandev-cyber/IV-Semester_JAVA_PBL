package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public static void assignTestToStudent(int testId, int studentId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO test_assignments(test_id, student_id) VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, testId);
            ps.setInt(2, studentId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAssignedTestsForStudent(int studentId) {
        List<String> tests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT t.test_name FROM tests t JOIN test_assignments ta ON t.id = ta.test_id WHERE ta.student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tests.add(rs.getString("test_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tests;
    }
}
