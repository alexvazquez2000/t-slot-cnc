package com.t_slot_cnc.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies MachineService loads inch values from machine.properties and scales
 * them correctly for mm mode. Values are taken from specs/machine.properties.
 */
class MachineServiceTest {

    @Test
    void inches_endMillDiameter() {
        assertEquals(0.25, new MachineService("inches").getEndMillDiameter(), 1e-6);
    }

    @Test
    void inches_feedRate() {
        assertEquals(35, new MachineService("inches").getFeedRate());
    }

    @Test
    void inches_spindleSpeed() {
        assertEquals(7, new MachineService("inches").getSpindleSpeed());
    }

    @Test
    void inches_cutDepthPerPass() {
        assertEquals(0.04, new MachineService("inches").getCutDepthPerPass(), 1e-6);
    }

    @Test
    void inches_zGapAbove() {
        assertEquals(0.1, new MachineService("inches").getzGapAbove(), 1e-6);
    }

    @Test
    void inches_drillFeedRate() {
        assertEquals(15, new MachineService("inches").getDrillFeedRate(), 1e-6);
    }

    @Test
    void mm_scalesEndMillDiameter() {
        assertEquals(0.25 * 25.4, new MachineService("mm").getEndMillDiameter(), 1e-6);
    }

    @Test
    void mm_scalesFeedRate() {
        assertEquals((int) (35 * 25.4), new MachineService("mm").getFeedRate());
    }

    @Test
    void mm_spindleSpeedUnchanged() {
        assertEquals(7, new MachineService("mm").getSpindleSpeed());
    }

    @Test
    void mm_scalesCutDepthPerPass() {
        assertEquals(0.04 * 25.4, new MachineService("mm").getCutDepthPerPass(), 1e-6);
    }

    @Test
    void mm_scalesZGapAbove() {
        assertEquals(0.1 * 25.4, new MachineService("mm").getzGapAbove(), 1e-6);
    }
}
