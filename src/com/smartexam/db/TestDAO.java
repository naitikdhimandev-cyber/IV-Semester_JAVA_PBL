package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestDAO {

    public static int createTest(String testName, int teacherId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO tests(test_name, created_by) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, testName);
            ps.setInt(2, teacherId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<String> getAllTests() {
        List<String> tests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT test_name FROM tests";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tests.add(rs.getString("test_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tests;
    }

    public static int getTestIdByName(String testName) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id FROM tests WHERE test_name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, testName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
