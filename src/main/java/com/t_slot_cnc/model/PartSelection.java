package com.t_slot_cnc.model;

/**
 * Snapshot of the user's current choices in the left panel, used to render the
 * hole-layout diagram.
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class PartSelection {

	private final Extrusion extrusion;
	private final HoleType holeType;
	private final int numColumns;
	private final int numRows;
	private final int heightMultiplier;
	private final boolean[][] selectedHoles;

	public PartSelection(Extrusion extrusion, HoleType holeType, int numColumns, int numRows,
			int heightMultiplier, boolean[][] selectedHoles) {
		this.extrusion = extrusion;
		this.holeType = holeType;
		this.numColumns = numColumns;
		this.numRows = numRows;
		this.heightMultiplier = heightMultiplier;
		this.selectedHoles = selectedHoles;
	}

	public Extrusion getExtrusion() {
		return extrusion;
	}

	public HoleType getHoleType() {
		return holeType;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public int getNumRows() {
		return numRows;
	}

	public int getHeightMultiplier() {
		return heightMultiplier;
	}

	/**
	 * @return 2×4 grid: selectedHoles[row][col], both zero-indexed
	 */
	public boolean[][] getSelectedHoles() {
		return selectedHoles;
	}

	/** Convenience: is a specific position selected and within the active grid? */
	public boolean isSelected(int row, int col) {
		if (row >= numRows || col >= numColumns) return false;
		return selectedHoles[row][col];
	}

}
