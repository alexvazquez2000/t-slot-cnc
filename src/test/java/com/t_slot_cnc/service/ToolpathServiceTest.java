package com.t_slot_cnc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ToolpathServiceTest {

    private ToolpathService toolpathService;
    private MachineService machine;

    @BeforeEach
    void setUp() {
        toolpathService = new ToolpathService();
        machine = new MachineService("inches");
    }

    // ── header ───────────────────────────────────────────────────────────────

    @Test
    void header_inches_setsG20() {
        assertTrue(toolpathService.header("inches", machine).contains("G20"));
    }

    @Test
    void header_mm_setsG21() {
        MachineService mmMachine = new MachineService("mm");
        String h = toolpathService.header("mm", mmMachine);
        assertTrue(h.contains("G21"));
        assertFalse(h.contains("G20"));
    }

    @Test
    void header_containsAbsoluteModeAndPlane() {
        String h = toolpathService.header("inches", machine);
        assertTrue(h.contains("G90"), "should set absolute positioning");
        assertTrue(h.contains("G17"), "should select XY plane");
    }

    @Test
    void header_containsSpindleStart() {
        assertTrue(toolpathService.header("inches", machine).contains("M3"));
    }

    @Test
    void header_homesXY() {
        assertTrue(toolpathService.header("inches", machine).contains("G00 X0.0 Y0.0"));
    }

    @Test
    void header_invalidUnits_throws() {
        assertThrows(RuntimeException.class, () -> toolpathService.header("cm", machine));
    }

    // ── tail ─────────────────────────────────────────────────────────────────

    @Test
    void tail_containsProgramEnd() {
        assertTrue(toolpathService.tail(machine).contains("M30"));
    }

    @Test
    void tail_returnsToHome() {
        assertTrue(toolpathService.tail(machine).contains("G00 X0 Y0"));
    }

    // ── counterbore ──────────────────────────────────────────────────────────

    @Test
    void counterbore_usesClockwiseArc() {
        String path = toolpathService.counterbore(machine, 0.75, 0.75, 0.73, 0.12, false);
        assertTrue(path.contains("G02"));
    }

    @Test
    void counterbore_includesInitialPositioningMove() {
        String path = toolpathService.counterbore(machine, 0.75, 0.75, 0.73, 0.12, false);
        assertTrue(path.contains("G01"));
    }

    @Test
    void counterbore_withDivot_includesDivotComment() {
        String path = toolpathService.counterbore(machine, 0.75, 0.75, 0.73, 0.12, true);
        assertTrue(path.contains("divot"));
    }

    @Test
    void counterbore_withoutDivot_noDivotComment() {
        String path = toolpathService.counterbore(machine, 0.75, 0.75, 0.73, 0.12, false);
        assertFalse(path.contains("divot"));
    }

    // ── accessHole ───────────────────────────────────────────────────────────

    @Test
    void accessHole_usesCounterClockwiseArc() {
        String path = toolpathService.accessHole(machine, 0.75, 0.75, 0.5, 0.5, 0.2);
        assertTrue(path.contains("G03"));
    }

    @Test
    void accessHole_isNonEmpty() {
        String path = toolpathService.accessHole(machine, 0.75, 0.75, 0.5, 0.5, 0.2);
        assertFalse(path.isEmpty());
    }

    // ── drillHole ────────────────────────────────────────────────────────────

    @Test
    void drillHole_producesLinearPlungeMove() {
        String path = toolpathService.drillHole(machine, 0.75, 0.75, 0.5, 0.5);
        assertTrue(path.contains("G01"));
    }

    // ── generateReturnToVice ─────────────────────────────────────────────────

    @Test
    void generateReturnToVice_containsCoordinates() {
        String code = toolpathService.generateReturnToVice(10.0, 20.0);
        assertTrue(code.contains("X10.000"));
        assertTrue(code.contains("Y20.000"));
    }

    @Test
    void generateReturnToVice_homesBeforeMoving() {
        String code = toolpathService.generateReturnToVice(10.0, 20.0);
        assertTrue(code.contains("G28"));
    }

    @Test
    void generateReturnToVice_endsProgramWithM30() {
        String code = toolpathService.generateReturnToVice(10.0, 20.0);
        assertTrue(code.contains("M30"));
    }
}
