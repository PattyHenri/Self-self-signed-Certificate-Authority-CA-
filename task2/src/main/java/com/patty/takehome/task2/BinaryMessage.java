package com.patty.takehome.task2;

public record BinaryMessage(
        String email,
        long lastSeenEpochMillis,
        String ip,
        int port
) {}
