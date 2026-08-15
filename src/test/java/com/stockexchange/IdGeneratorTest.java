package com.stockexchange;

import com.stockexchange.Util.IdGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IdGeneratorTest {
    @Test
    void shouldGenerateUniqueIds() {

        String id1 = IdGenerator.generateId();
        String id2 = IdGenerator.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }
}
