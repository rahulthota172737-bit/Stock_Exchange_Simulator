package com.stockexchange.Util;

import java.util.UUID;

public class IdGenerator {
    private IdGenerator() {
        // Utility class
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
