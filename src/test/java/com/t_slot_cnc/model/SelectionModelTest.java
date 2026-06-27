package com.t_slot_cnc.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectionModelTest {

    private SelectionModel model;

    @BeforeEach
    void setUp() {
        model = new SelectionModel();
    }

    @Test
    void initialState_allHolesSelected() {
        boolean[][] holes = model.getSelectedHoles();
        for (boolean[] row : holes)
            for (boolean cell : row)
                assertTrue(cell, "all holes should be selected on construction");
    }

    @Test
    void setHoleSelected_false_reflectsInGrid() {
        model.setHoleSelected(0, 1, false);
        assertFalse(model.getSelectedHoles()[0][1]);
    }

    @Test
    void setHoleSelected_doesNotAffectAdjacentCells() {
        model.setHoleSelected(0, 1, false);
        assertTrue(model.getSelectedHoles()[0][0], "adjacent hole should remain selected");
        assertTrue(model.getSelectedHoles()[0][2], "adjacent hole should remain selected");
    }

    @Test
    void resetSelectedHoles_setsInScopeCellsTrue() {
        model.setNumColumns(2);
        model.setNumRows(1);
        boolean[][] holes = model.getSelectedHoles();
        assertTrue(holes[0][0]);
        assertTrue(holes[0][1]);
    }

    @Test
    void resetSelectedHoles_clearsOutOfScopeCells() {
        model.setNumColumns(2);
        model.setNumRows(1);
        boolean[][] holes = model.getSelectedHoles();
        assertFalse(holes[0][2], "col 2 is outside the 2-column scope");
        assertFalse(holes[1][0], "row 1 is outside the 1-row scope");
    }

    @Test
    void setNumColumns_triggersReset() {
        model.setHoleSelected(0, 0, false);
        model.setNumColumns(1);
        assertTrue(model.getSelectedHoles()[0][0], "reset should re-select in-scope holes");
    }

    @Test
    void setNumRows_triggersReset() {
        model.setHoleSelected(0, 0, false);
        model.setNumRows(1);
        assertTrue(model.getSelectedHoles()[0][0], "reset should re-select in-scope holes");
    }

    @Test
    void heightMultiplier_defaultIsOne() {
        assertEquals(1, model.getHeightMultiplier());
    }

    @Test
    void heightMultiplier_roundTrip() {
        model.setHeightMultiplier(2);
        assertEquals(2, model.getHeightMultiplier());
    }

    @Test
    void selectedSeries_roundTrip() {
        model.setSelectedSeries("15-S");
        assertEquals("15-S", model.getSelectedSeries());
    }

    @Test
    void holeType_roundTrip() {
        model.setSelectedHoleType(HoleType.COUNTERBORE);
        assertEquals(HoleType.COUNTERBORE, model.getHoleType());
    }
}
