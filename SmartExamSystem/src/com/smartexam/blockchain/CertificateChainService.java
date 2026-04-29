package com.smartexam.blockchain;

import com.smartexam.db.CertificateDAO;

import java.time.Instant;

public final class CertificateChainService {

    private CertificateChainService() {
    }

    public static CertificateDAO.CertificateRecord issue(int studentId, String studentName, int testId,
                                                         String testName, int score, int totalQuestions,
                                                         int resultId) {
        String issuedAt = Instant.now().toString();
        String prevHash = CertificateDAO.getTipBlockHashOrGenesis();
        String payload = CertificateDAO.canonicalPayload(
                studentId, studentName, testId, testName, score, totalQuestions, issuedAt, resultId
        );
        String blockHash = CustomChainHasher.blockHash(prevHash, payload);
        String certificateId = blockHash;

        boolean ok = CertificateDAO.insertCertificate(
                certificateId, studentId, testId, studentName, testName, score, totalQuestions,
                issuedAt, resultId, prevHash, blockHash
        );
        if (!ok) {
            return null;
        }
        return new CertificateDAO.CertificateRecord(
                certificateId, studentId, testId, studentName, testName, score, totalQuestions,
                issuedAt, resultId, prevHash, blockHash
        );
    }
}
