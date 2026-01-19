MERGE INTO users (email, last_seen, ip, port)
KEY(email)
VALUES ('alice@example.com', 0, '0.0.0.0', 0);

MERGE INTO users (email, last_seen, ip, port)
KEY(email)
VALUES ('bob@example.com', 0, '0.0.0.0', 0);
