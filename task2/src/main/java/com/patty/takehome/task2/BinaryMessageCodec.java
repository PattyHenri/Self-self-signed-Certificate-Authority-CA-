package com.patty.takehome.task2;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public final class BinaryMessageCodec {

    private BinaryMessageCodec() {}

    /**
     * Wire format (v1) used by this Task2 decoder:
     *  [0]      : 1 byte   version = 1
     *  [1..2]   : 2 bytes  unsigned emailLength (big-endian)
     *  [..]     : N bytes  email UTF-8
     *  [..]     : 8 bytes  lastSeenEpochMillis (big-endian long)
     *  [..]     : 4 bytes  ip (IPv4 bytes)
     *  [..]     : 2 bytes  unsigned port (big-endian)
     *
     * Minimum length = 1 + 2 + 0 + 8 + 4 + 2 = 17 bytes
     *
     * IMPORTANT:
     * If your server-side BinaryMessageCodec uses a different format,
     * copy the exact encode/decode logic here to match it 1:1.
     */
    public static BinaryMessage decodeV1(byte[] payload) {
        if (payload == null) throw new IllegalArgumentException("payload is null");
        if (payload.length < 17) throw new IllegalArgumentException("payload too short: " + payload.length);

        ByteBuffer bb = ByteBuffer.wrap(payload).order(ByteOrder.BIG_ENDIAN);

        int version = Byte.toUnsignedInt(bb.get());
        if (version != 1) throw new IllegalArgumentException("unsupported version: " + version);

        int emailLen = Short.toUnsignedInt(bb.getShort());
        if (emailLen < 0) throw new IllegalArgumentException("negative emailLen?");
        if (bb.remaining() < emailLen + 8 + 4 + 2) {
            throw new IllegalArgumentException("payload truncated for emailLen=" + emailLen);
        }

        byte[] emailBytes = new byte[emailLen];
        bb.get(emailBytes);
        String email = new String(emailBytes, StandardCharsets.UTF_8);

        long lastSeenEpochMillis = bb.getLong();

        byte[] ipBytes = new byte[4];
        bb.get(ipBytes);
        String ip;
        try {
            ip = InetAddress.getByAddress(ipBytes).getHostAddress();
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid IPv4 bytes");
        }

        int port = Short.toUnsignedInt(bb.getShort());
        if (port < 0 || port > 65535) throw new IllegalArgumentException("invalid port: " + port);

        return new BinaryMessage(email, lastSeenEpochMillis, ip, port);
    }
}
