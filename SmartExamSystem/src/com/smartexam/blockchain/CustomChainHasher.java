package com.smartexam.blockchain;

import java.nio.charset.StandardCharsets;

public final class CustomChainHasher {

    private CustomChainHasher() {
    }

    public static final String GENESIS_PREV_HASH = digest256("SMARTEXAM_CERT_CHAIN_GENESIS_V1");


    public static String digest256(String input) {
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        long v0 = 0x6a09e667f3bcc908L;
        long v1 = 0xbb67ae8584caa73bL;
        long v2 = 0x3c6ef372fe94f82bL;
        long v3 = 0xa54ff53a5f1d36f1L;
        for (int i = 0; i < data.length; i++) {
            v0 ^= (long) (data[i] & 0xFF) << ((i % 8) * 8);
            v1 = Long.rotateLeft(v1 + v0 * 0x9E3779B97F4A7C15L, 31) ^ v2;
            v2 = Long.rotateLeft(v2 + v3, 17) ^ (long) (i * 0x85EBCA6B);
            v3 = Long.rotateLeft(v3 + v1, 13) ^ v0;
        }
        for (int r = 0; r < 24; r++) {
            v0 += v1;
            v1 = Long.rotateLeft(v1 ^ v2, 27);
            v2 += v3;
            v3 = Long.rotateLeft(v3 ^ v0, 19);
            long t = v0;
            v0 = v2;
            v2 = t;
        }
        return String.format("%016x%016x%016x%016x", v0, v1, v2, v3);
    }

    public static String blockHash(String previousBlockHash, String canonicalPayload) {
        return digest256(previousBlockHash + "||" + canonicalPayload);
    }
}
