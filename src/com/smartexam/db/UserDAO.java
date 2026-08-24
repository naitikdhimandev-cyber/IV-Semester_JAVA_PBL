package com.smartexam.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static String getUsernameById(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username FROM users WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getUserIdByUsername(String username) {
        if (username == null) {
            return -1;
        }
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return -1;
            }
            String sql = "SELECT id FROM users WHERE LOWER(TRIM(username)) = LOWER(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static boolean userExists(String username) {
        if (username == null) {
            return false;
        }
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return false;
            }
            String sql = "SELECT id FROM users WHERE LOWER(TRIM(username)) = LOWER(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createUser(String username, String passwordHash, String role) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String> getAllStudentUsernames() {
        List<String> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username FROM users WHERE role='student'";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
