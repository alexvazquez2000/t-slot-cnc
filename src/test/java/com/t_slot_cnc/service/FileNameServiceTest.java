package com.t_slot_cnc.service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileNameServiceTest {

    private Extrusion ext;
    private AccessHole accessHole;
    private Counterbore counterbore;

    @BeforeEach
    void setUp() {
        ext = new Extrusion();
        ext.setId("15-S");
        ext.setUnits("inches");
        ext.setWidth(1.5);

        accessHole = new AccessHole();
        accessHole.setDiameter(0.5);
        accessHole.setyOffset(0.75);
        ext.setAccessHole(accessHole);

        counterbore = new Counterbore();
        counterbore.setDiameter(0.73);
        counterbore.setDepth(0.12);
        counterbore.setyOffset(0.75);
        ext.setCounterbore(counterbore);
    }

    @Test
    void nameCounterbore_oneColumn() {
        assertEquals("output/15-S/counterbore/15X_cb_A_.nc",
                FileNameService.nameCounterbore(ext, counterbore, 1));
    }

    @Test
    void nameCounterbore_twoColumns() {
        assertEquals("output/15-S/counterbore/15X_cb_A_B_.nc",
                FileNameService.nameCounterbore(ext, counterbore, 2));
    }

    @Test
    void nameDrillHole_oneColumnOneRow_multiplierOne() {
        assertEquals("output/15-S/drill_hole/15_dh_A_1.nc",
                FileNameService.nameDrillHole(ext, 1, 1, 1));
    }

    @Test
    void nameDrillHole_multiplierTwo_doublesSeriesPrefix() {
        assertEquals("output/15-S/drill_hole/30_dh_A_1.nc",
                FileNameService.nameDrillHole(ext, 1, 1, 2));
    }

    @Test
    void nameDrillHole_twoColumnsOneRow() {
        assertEquals("output/15-S/drill_hole/15_dh_A_B_1.nc",
                FileNameService.nameDrillHole(ext, 2, 1, 1));
    }

    @Test
    void nameAccessHole_twoColumns_oneRow_multiplierOne() {
        assertEquals("output/15-S/access_hole/15X_ah_A_B_1_1.nc",
                FileNameService.nameAccessHole(ext, accessHole, 2, 1, 1));
    }

    @Test
    void nameDrillHoleSelection_columnAOnly() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        assertEquals("output/15-S/drill_hole/15_dh_A_1.nc",
                FileNameService.nameDrillHoleSelection(ext, selected, 1));
    }

    @Test
    void nameCounterboreSelection_columnsAAndC() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        selected[0][2] = true;
        assertEquals("output/15-S/counterbore/15X_cb_A_C_.nc",
                FileNameService.nameCounterboreSelection(ext, selected));
    }

    @Test
    void nameDrillHoleSelection_twoRows_multiplierTwo_doublesPrefix() {
        boolean[][] selected = new boolean[2][4];
        selected[0][0] = true;
        selected[1][0] = true;
        assertEquals("output/15-S/drill_hole/30_dh_A_2.nc",
                FileNameService.nameDrillHoleSelection(ext, selected, 2));
    }
}
