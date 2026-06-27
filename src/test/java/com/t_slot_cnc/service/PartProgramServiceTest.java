package com.t_slot_cnc.service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PartProgramServiceTest {

    @Mock
    GCodeFileService gCodeFileService;

    private PartProgramService service;
    private Extrusion ext;

    @BeforeEach
    void setUp() {
        service = new PartProgramService(new ToolpathService(), new PartDescriptionService(), gCodeFileService);

        ext = new Extrusion();
        ext.setId("15-S");
        ext.setUnits("inches");
        ext.setWidth(1.5);
        ext.setDepthToTopOfSlot(0.2);

        AccessHole ah = new AccessHole();
        ah.setDiameter(0.5);
        ah.setyOffset(0.75);
        ext.setAccessHole(ah);

        Counterbore cb = new Counterbore();
        cb.setDiameter(0.73);
        cb.setDepth(0.12);
        cb.setyOffset(0.75);
        ext.setCounterbore(cb);
    }

    // ── computeHoleDepth ─────────────────────────────────────────────────────

    @Test
    void computeHoleDepth_multiplierOne_returnsExpectedDepth() {
        MachineService machine = new MachineService("inches");
        // 1.5 * 0.85 + 0.04 = 1.315, which is less than width 1.5
        double depth = PartProgramService.computeHoleDepth(ext, machine, 1);
        assertEquals(1.315, depth, 1e-6);
    }

    @Test
    void computeHoleDepth_multiplierTwo_deeperThanMultiplierOne() {
        MachineService machine = new MachineService("inches");
        double depth1 = PartProgramService.computeHoleDepth(ext, machine, 1);
        double depth2 = PartProgramService.computeHoleDepth(ext, machine, 2);
        assertTrue(depth2 > depth1);
    }

    @Test
    void computeHoleDepth_invalidMultiplier_throws() {
        MachineService machine = new MachineService("inches");
        assertThrows(RuntimeException.class, () -> PartProgramService.computeHoleDepth(ext, machine, 3));
    }

    // ── buildDrillHoleText ───────────────────────────────────────────────────

    @Test
    void buildDrillHoleText_singleHole_containsHeaderAndTail() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        String code = service.buildDrillHoleText(ext, selected, 1);
        assertTrue(code.contains("G20"), "inches program should declare G20");
        assertTrue(code.contains("M30"), "program should end with M30");
    }

    @Test
    void buildDrillHoleText_singleHole_containsDrillMove() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        String code = service.buildDrillHoleText(ext, selected, 1);
        assertTrue(code.contains("G01"), "should contain a linear plunge move");
    }

    @Test
    void buildDrillHoleText_noHolesSelected_stillHasHeaderAndTail() {
        boolean[][] selected = new boolean[2][4]; // all false
        String code = service.buildDrillHoleText(ext, selected, 1);
        assertTrue(code.contains("G20"));
        assertTrue(code.contains("M30"));
    }

    @Test
    void buildDrillHoleText_multiplierTwo_producesDeeper() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        String code1 = service.buildDrillHoleText(ext, selected, 1);
        String code2 = service.buildDrillHoleText(ext, selected, 2);
        assertFalse(code1.equals(code2), "multiplier=2 should produce different G-code");
    }

    // ── buildCounterboreText ─────────────────────────────────────────────────

    @Test
    void buildCounterboreText_singleHole_containsG02Arc() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        String code = service.buildCounterboreText(ext, selected);
        assertTrue(code.contains("G02"), "counterbore should use clockwise G02 arc");
    }

    @Test
    void buildCounterboreText_singleHole_containsHeaderAndTail() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        String code = service.buildCounterboreText(ext, selected);
        assertTrue(code.contains("G20"));
        assertTrue(code.contains("M30"));
    }

    @Test
    void buildCounterboreText_noCounterbore_returnsEmpty() {
        ext.setCounterbore(null);
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        assertTrue(service.buildCounterboreText(ext, selected).isEmpty());
    }
}
