package com.smartexam.auth;

import com.smartexam.db.DBConnection;
import com.smartexam.util.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginService {

    public static String authenticate(String user, String pass) {
        if (user == null || pass == null) {
            return null;
        }
        String u = user.trim();
        String p = pass.trim();
        if (u.isEmpty() || p.isEmpty()) {
            return null;
        }
        String passHash = HashUtil.md5(p);
        if (passHash == null) {
            return null;
        }
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                return null;
            }
            String sql = "SELECT role FROM users WHERE LOWER(TRIM(username)) = LOWER(?) AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u);
            ps.setString(2, passHash);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
