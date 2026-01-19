# Take-Home Test — mTLS PATCH + DB Update + UDP Broadcast (Task1) + UDP Listener (Task2)

This repository contains a multi-module Maven project:

- **server/** (Task 1) Spring Boot server over HTTPS with **mTLS**, validates client certificate CN, updates DB, broadcasts UDP.
- **client/** (Task 1) Java client sending an **empty HTTP PATCH** request using a TLS client certificate (mTLS).
- **task2/** (Task 2 - optional) UDP listener that receives broadcast packets and prints decoded fields.

The implementation follows the specification: the client sends an empty PATCH using mTLS, the server extracts CN from the client cert, validates it as email-like (HTTP 400 otherwise), looks up the user in DB (HTTP 403 if missing), updates `lastSeen` (epoch nanos), `ip`, `port`, then broadcasts a binary UDP message from port 6666 to receivers on port 6667. :contentReference[oaicite:1]{index=1}


---

## 1) Requirements

### Software
- Windows 11 Pro
- Java **21** (JDK) installed (Eclipse uses JavaSE-21)
- Eclipse IDE (Enterprise Java and Web Developers)
- OpenSSL available (Git Bash is easiest on Windows)
- (Optional) Docker Desktop (if you want to run server in Docker)
- Maven is optional for CLI usage — Eclipse m2e can build/run without global Maven installation.

### Ports used
- Server HTTPS: **8443**
- UDP broadcaster sends **FROM 6666** and broadcasts **TO 6667**
- UDP listener (task2) listens on **6667**

---

## 2) Project Layout

