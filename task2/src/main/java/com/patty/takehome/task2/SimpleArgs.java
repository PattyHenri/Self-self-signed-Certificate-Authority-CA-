package com.patty.takehome.task2;

import java.util.HashMap;
import java.util.Map;

public final class SimpleArgs {

    private SimpleArgs() {}

    // Supports: --key=value
    public static Map<String, String> parse(String[] args) {
        Map<String, String> map = new HashMap<>();
        if (args == null) return map;

        for (String a : args) {
            if (a == null) continue;
            if (a.startsWith("--") && a.contains("=")) {
                int i = a.indexOf('=');
                String k = a.substring(2, i).trim();
                String v = a.substring(i + 1).trim();
                if (!k.isEmpty()) map.put(k, v);
            }
        }
        return map;
    }
}
