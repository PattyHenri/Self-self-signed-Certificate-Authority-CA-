package com.patty.takehome.server.udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

public final class BinaryMessageCodec {

  private BinaryMessageCodec() {}

  /**
   * Simple binary format:
   *   int emailLen + emailBytes(UTF-8)
   *   long lastSeenEpochNanos
   *   int ipLen + ipBytes(UTF-8)
   *   int port
   */
  public static byte[] encode(String email, long lastSeenNanos, String ip, int port) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);

      byte[] emailBytes = email.getBytes(StandardCharsets.UTF_8);
      dos.writeInt(emailBytes.length);
      dos.write(emailBytes);

      dos.writeLong(lastSeenNanos);

      byte[] ipBytes = ip.getBytes(StandardCharsets.UTF_8);
      dos.writeInt(ipBytes.length);
      dos.write(ipBytes);

      dos.writeInt(port);

      dos.flush();
      return baos.toByteArray();
    } catch (Exception e) {
      // Should never happen with ByteArrayOutputStream
      throw new IllegalStateException("Failed to encode UDP message", e);
    }
  }
}
