# Secure Presence Service — mTLS PATCH API, User Activity Update, and UDP Broadcast (Task 1) + UDP Listener (Task 2)

This repository contains a multi-module Maven project implementing a secure client–server workflow using **mutual TLS (mTLS)** and a lightweight UDP broadcast mechanism.

## Modules
- **server/** (Task 1) Spring Boot HTTPS server with **mTLS**. Validates the client certificate, extracts the certificate CN, updates a user record in the database, and broadcasts a binary UDP message.
- **client/** (Task 1) Java client that authenticates with a **TLS client certificate** and sends an **empty HTTP PATCH** request to the server.
- **task2/** (Task 2 — optional) UDP listener that receives broadcast packets on port 6667 and prints decoded fields (with safe handling for malformed packets).

## End-to-end behavior
1. The client sends an **empty PATCH** request over **mTLS**.
2. The server extracts the **CN (Common Name)** from the client TLS certificate.
3. CN format is validated:
   - If CN does not resemble an email address → responds **HTTP 400**
4. The server looks up a user by email (CN):
   - If no matching user exists → responds **HTTP 403**
5. On success, the server updates the user record:
   - `lastSeen`: current time as **nanoseconds since Unix epoch**
   - `ip`: IP address used for the API call
   - `port`: port used for the API call
6. The server broadcasts a **binary message** from UDP **port 6666** to applications listening on UDP **port 6667**.

---
