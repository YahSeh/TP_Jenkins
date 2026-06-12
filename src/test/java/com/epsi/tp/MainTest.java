package com.epsi.tp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void mainShouldRunWithoutConfiguredEnvironment() {
        assertDoesNotThrow(() -> Main.main(new String[0]));
    }

}
