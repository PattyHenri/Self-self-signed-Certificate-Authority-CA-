package com.patty.takehome.server.db;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 320)
  private String email;

  @Column(name = "last_seen")
  private Long lastSeen;

  @Column(length = 64)
  private String ip;

  private Integer port;

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public Long getLastSeen() { return lastSeen; }
  public void setLastSeen(Long lastSeen) { this.lastSeen = lastSeen; }

  public String getIp() { return ip; }
  public void setIp(String ip) { this.ip = ip; }

  public Integer getPort() { return port; }
  public void setPort(Integer port) { this.port = port; }
}
