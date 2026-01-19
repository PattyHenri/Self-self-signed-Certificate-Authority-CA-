package com.patty.takehome.server.udp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UdpBroadcaster {

  @Value("${takehome.udp.sourcePort:6666}")
  private int sourcePort;

  @Value("${takehome.udp.targetPort:6667}")
  private int targetPort;

  @Value("${takehome.udp.broadcastAddress:255.255.255.255}")
  private String broadcastAddress;

  private DatagramSocket socket;
  private InetAddress target;

  @PostConstruct
  public void init() throws Exception {
    // Pre-initialized component bound to UDP port 6666
    this.socket = new DatagramSocket(sourcePort);
    this.socket.setBroadcast(true);
    this.target = InetAddress.getByName(broadcastAddress);
  }

  public void broadcast(byte[] payload) {
    try {
      DatagramPacket packet = new DatagramPacket(payload, payload.length, target, targetPort);
      socket.send(packet);
    } catch (Exception e) {
      // Don't crash request processing if UDP fails; just log
      System.err.println("UDP broadcast failed: " + e.getMessage());
    }
  }

  @PreDestroy
  public void shutdown() {
    if (socket != null && !socket.isClosed()) socket.close();
  }
}
