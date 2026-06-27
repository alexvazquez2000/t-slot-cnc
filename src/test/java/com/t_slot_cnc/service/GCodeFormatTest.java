package com.t_slot_cnc.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GCodeFormatTest {

    @Test
    void format_fourDecimals() {
        assertEquals("1.0000", GCodeFormat.format(1.0, 4));
    }

    @Test
    void format_twoDecimals_roundsCorrectly() {
        assertEquals("3.14", GCodeFormat.format(3.14159, 2));
    }

    @Test
    void format_zero_threeDecimals() {
        assertEquals("0.000", GCodeFormat.format(0.0, 3));
    }

    @Test
    void format_negativeValue() {
        assertEquals("-0.5000", GCodeFormat.format(-0.5, 4));
    }
}
