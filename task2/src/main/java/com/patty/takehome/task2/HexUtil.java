package com.patty.takehome.task2;

public final class HexUtil {

    private HexUtil() {}

    public static String toHex(byte[] bytes, int maxBytes) {
        if (bytes == null) return "(null)";
        int n = Math.min(bytes.length, Math.max(0, maxBytes));
        StringBuilder sb = new StringBuilder(n * 2);
        for (int i = 0; i < n; i++) {
            int v = bytes[i] & 0xFF;
            sb.append(Character.forDigit(v >>> 4, 16));
            sb.append(Character.forDigit(v & 0x0F, 16));
        }
        if (bytes.length > n) sb.append("...");
        return sb.toString();
    }
}
