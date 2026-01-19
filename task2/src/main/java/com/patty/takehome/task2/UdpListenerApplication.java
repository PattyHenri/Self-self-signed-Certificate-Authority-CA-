package com.patty.takehome.task2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class UdpListenerApplication {

    private static final int DEFAULT_PORT = 6667;

    public static void main(String[] args) throws Exception {
        Map<String, String> cli = SimpleArgs.parse(args);

        // Bonus: configurable by CLI OR .env config file
        // Priority: CLI --port > config file UDP_PORT > default 6667
        String configPath = cli.get("config"); // e.g. --config=task2.env
        Map<String, String> env = (configPath != null) ? DotEnv.load(configPath) : Map.of();

        int port = DEFAULT_PORT;
        if (env.containsKey("UDP_PORT")) {
            port = safeParseInt(env.get("UDP_PORT"), DEFAULT_PORT);
        }
        if (cli.containsKey("port")) {
            port = safeParseInt(cli.get("port"), port);
        }

        System.out.println("Task2 UDP listener starting...");
        System.out.println("Listening for UDP broadcasts on port: " + port);
        System.out.println("Config file: " + (configPath == null ? "(none)" : configPath));
        System.out.println("Press Ctrl+C to stop.\n");

        // Bind on all interfaces
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(port));

        byte[] buf = new byte[2048]; // enough for typical small payloads

        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            byte[] payload = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), packet.getOffset(), payload, 0, packet.getLength());

            String srcIp = packet.getAddress().getHostAddress();
            int srcPort = packet.getPort();

            try {
                BinaryMessage msg = BinaryMessageCodec.decodeV1(payload);

                // Convert lastSeen (epoch millis) to human readable
                String lastSeenHuman = DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        .withZone(ZoneId.systemDefault())
                        .format(Instant.ofEpochMilli(msg.lastSeenEpochMillis()));

                System.out.println("=== UDP Packet Received ===");
                System.out.println("Source: " + srcIp + ":" + srcPort);
                System.out.println("email: " + msg.email());
                System.out.println("lastSeen: " + lastSeenHuman + " (epochMillis=" + msg.lastSeenEpochMillis() + ")");
                System.out.println("ip: " + msg.ip());
                System.out.println("port: " + msg.port());
                System.out.println();
            } catch (Exception ex) {
                // MUST NOT crash on malformed packets
                System.out.println("=== Malformed packet discarded ===");
                System.out.println("Source: " + srcIp + ":" + srcPort);
                System.out.println("Reason: " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
                System.out.println("Payload (hex, first 64 bytes): " + HexUtil.toHex(payload, 64));
                System.out.println("Payload (ascii, first 64 bytes): " + safeAscii(payload, 64));
                System.out.println();
            }
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return fallback; }
    }

    private static String safeAscii(byte[] b, int max) {
        int n = Math.min(b.length, max);
        byte[] slice = new byte[n];
        System.arraycopy(b, 0, slice, 0, n);
        String s = new String(slice, StandardCharsets.US_ASCII);
        return s.replaceAll("[^\\x20-\\x7E]", ".");
    }
}
