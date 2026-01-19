package com.patty.takehome.task2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public final class DotEnv {

    private DotEnv() {}

    // Simple KEY=VALUE parser; ignores blank lines and comments (# ...)
    public static Map<String, String> load(String path) {
        Map<String, String> map = new HashMap<>();
        if (path == null || path.isBlank()) return map;

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String s = line.trim();
                if (s.isEmpty()) continue;
                if (s.startsWith("#")) continue;
                int eq = s.indexOf('=');
                if (eq <= 0) continue;

                String key = s.substring(0, eq).trim();
                String val = s.substring(eq + 1).trim();

                // remove optional quotes
                if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                    if (val.length() >= 2) val = val.substring(1, val.length() - 1);
                }
                map.put(key, val);
            }
        } catch (Exception ignored) {
            // Do not crash if config missing; just return empty
        }
        return map;
    }
}
