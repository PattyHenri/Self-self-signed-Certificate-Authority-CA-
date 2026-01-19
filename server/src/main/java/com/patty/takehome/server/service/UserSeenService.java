package com.patty.takehome.server.service;

import com.patty.takehome.server.db.User;
import com.patty.takehome.server.db.UserRepository;
import com.patty.takehome.server.udp.BinaryMessageCodec;
import com.patty.takehome.server.udp.UdpBroadcaster;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSeenService {

  private final UserRepository userRepository;
  private final UdpBroadcaster udpBroadcaster;

  public UserSeenService(UserRepository userRepository, UdpBroadcaster udpBroadcaster) {
    this.userRepository = userRepository;
    this.udpBroadcaster = udpBroadcaster;
  }

  public static boolean looksLikeEmail(String cn) {
    // Simple but effective email-ish check
    return cn != null && cn.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
  }

  public static long epochNanosNow() {
    Instant now = Instant.now();
    return now.getEpochSecond() * 1_000_000_000L + now.getNano();
  }

  @Transactional
  public Optional<User> updateLastSeen(String email, String ip, int port) {
    Optional<User> maybeUser = userRepository.findByEmail(email);
    if (maybeUser.isEmpty()) return Optional.empty();

    User u = maybeUser.get();
    long lastSeen = epochNanosNow();
    u.setLastSeen(lastSeen);
    u.setIp(ip);
    u.setPort(port);

    User saved = userRepository.save(u);

    byte[] msg = BinaryMessageCodec.encode(saved.getEmail(), saved.getLastSeen(), saved.getIp(), saved.getPort());
    udpBroadcaster.broadcast(msg);

    return Optional.of(saved);
  }
}
