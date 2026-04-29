package com.smartexam.db;

import com.smartexam.blockchain.CustomChainHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAO {

    public static final class CertificateRecord {
        public final String certificateId;
        public final int studentId;
        public final int testId;
        public final String studentName;
        public final String testName;
        public final int score;
        public final int totalQuestions;
        public final String issuedAt;
        public final int resultId;
        public final String prevHash;
        public final String blockHash;

        public CertificateRecord(String certificateId, int studentId, int testId, String studentName,
                                   String testName, int score, int totalQuestions, String issuedAt,
                                   int resultId, String prevHash, String blockHash) {
            this.certificateId = certificateId;
            this.studentId = studentId;
            this.testId = testId;
            this.studentName = studentName;
            this.testName = testName;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.issuedAt = issuedAt;
            this.resultId = resultId;
            this.prevHash = prevHash;
            this.blockHash = blockHash;
        }
    }

    public static String canonicalPayload(int studentId, String studentName, int testId, String testName,
                                            int score, int totalQuestions, String issuedAt, int resultId) {
        return studentId + "|" + studentName + "|" + testId + "|" + testName + "|" + score + "|"
                + totalQuestions + "|" + issuedAt + "|" + resultId;
    }

    /**
     * Last block hash in the chain, or genesis anchor if no certificates yet.
     */
    public static String getTipBlockHashOrGenesis() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT block_hash FROM certificates ORDER BY id DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("block_hash");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CustomChainHasher.GENESIS_PREV_HASH;
    }

    public static boolean insertCertificate(String certificateId, int studentId, int testId,
                                          String studentName, String testName, int score, int totalQuestions,
                                          String issuedAt, int resultId, String prevHash, String blockHash) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                INSERT INTO certificates(
                    certificate_id, student_id, test_id, student_name, test_name, score, total_questions,
                    issued_at, result_id, prev_hash, block_hash
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?)
                """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, certificateId);
            ps.setInt(2, studentId);
            ps.setInt(3, testId);
            ps.setString(4, studentName);
            ps.setString(5, testName);
            ps.setInt(6, score);
            ps.setInt(7, totalQuestions);
            ps.setString(8, issuedAt);
            ps.setInt(9, resultId);
            ps.setString(10, prevHash);
            ps.setString(11, blockHash);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static CertificateRecord findByCertificateId(String certificateId) {
        if (certificateId == null) {
            return null;
        }
        String id = certificateId.trim();
        if (id.isEmpty()) {
            return null;
        }
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT certificate_id, student_id, test_id, student_name, test_name, score, total_questions,
                       issued_at, result_id, prev_hash, block_hash
                FROM certificates WHERE certificate_id = ?
                """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new CertificateRecord(
                        rs.getString("certificate_id"),
                        rs.getInt("student_id"),
                        rs.getInt("test_id"),
                        rs.getString("student_name"),
                        rs.getString("test_name"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getString("issued_at"),
                        rs.getInt("result_id"),
                        rs.getString("prev_hash"),
                        rs.getString("block_hash")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CertificateRecord> listForStudent(int studentId) {
        List<CertificateRecord> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = """
                SELECT certificate_id, student_id, test_id, student_name, test_name, score, total_questions,
                       issued_at, result_id, prev_hash, block_hash
                FROM certificates WHERE student_id = ? ORDER BY id DESC
                """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new CertificateRecord(
                        rs.getString("certificate_id"),
                        rs.getInt("student_id"),
                        rs.getInt("test_id"),
                        rs.getString("student_name"),
                        rs.getString("test_name"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getString("issued_at"),
                        rs.getInt("result_id"),
                        rs.getString("prev_hash"),
                        rs.getString("block_hash")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * True if stored fields recompute to the same block hash and id matches chain tip semantics.
     */
    public static boolean verifyIntegrity(CertificateRecord r) {
        if (r == null) {
            return false;
        }
        String payload = canonicalPayload(
                r.studentId, r.studentName, r.testId, r.testName, r.score, r.totalQuestions,
                r.issuedAt, r.resultId
        );
        String expected = CustomChainHasher.blockHash(r.prevHash, payload);
        return expected.equals(r.blockHash) && r.blockHash.equals(r.certificateId);
    }

    /**
     * Optional: previous block in chain exists (not for genesis prev).
     */
    public static boolean previousBlockExists(String prevHash) {
        if (prevHash == null || prevHash.equals(CustomChainHasher.GENESIS_PREV_HASH)) {
            return true;
        }
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT 1 FROM certificates WHERE block_hash = ? LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, prevHash);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
