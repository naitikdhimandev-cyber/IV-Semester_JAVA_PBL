package com.smartexam.db;

import java.sql.Connection;
import java.sql.Statement;

public class DBInitializer {
    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE,
                            password TEXT,
                            role TEXT
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS tests (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            test_name TEXT,
                            created_by INTEGER
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS questions (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            test_id INTEGER,
                            question TEXT,
                            option_a TEXT,
                            option_b TEXT,
                            option_c TEXT,
                            option_d TEXT,
                            correct_option TEXT
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS test_assignments (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            test_id INTEGER,
                            student_id INTEGER
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS results (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            student_id INTEGER,
                            test_id INTEGER,
                            score INTEGER,
                            timestamp TEXT
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS certificates (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            certificate_id TEXT NOT NULL UNIQUE,
                            student_id INTEGER NOT NULL,
                            test_id INTEGER NOT NULL,
                            student_name TEXT NOT NULL,
                            test_name TEXT NOT NULL,
                            score INTEGER NOT NULL,
                            total_questions INTEGER NOT NULL,
                            issued_at TEXT NOT NULL,
                            result_id INTEGER NOT NULL,
                            prev_hash TEXT NOT NULL,
                            block_hash TEXT NOT NULL
                        )
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS violations (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            student_id INTEGER,
                            test_id INTEGER,
                            violation_type TEXT,
                            violation_details TEXT,
                            timestamp TEXT
                        )
                    """);

            stmt.execute("""
                        INSERT OR IGNORE INTO users(id, username, password, role)
                        VALUES (1, 'student', '202cb962ac59075b964b07152d234b70', 'student')
                    """);

            stmt.execute("""
                        INSERT OR IGNORE INTO users(id, username, password, role)
                        VALUES (2, 'teacher', '202cb962ac59075b964b07152d234b70', 'teacher')
                    """);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
