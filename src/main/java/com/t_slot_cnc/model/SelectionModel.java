package com.t_slot_cnc.model;

import java.util.Arrays;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class SelectionModel {
	private String selectedSeries;
	private HoleType holeType;
	private int numColumns;
	private int numRows;
	private int heightMultiplier = 1;
	private boolean makeDivot = true;
	//true - we zero the CNC machine to align to the left of 
	//the extrusion's left edge to meet the end of the end mill 
	//and edge of extrusion to meet the top of the end mill.
	// if false then there is no offset, all holes are relative to
	// the current CNC position
	private boolean useOffset = true;
	private boolean[][] selectedHoles = allSelected();

	public String getSelectedSeries() {
		return selectedSeries;
	}

	public void setSelectedSeries(String selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	public HoleType getHoleType() {
		return holeType;
	}

	public void setSelectedHoleType(HoleType holeType) {
		this.holeType = holeType;
	}

	/**
	 * @return the number of columns (1 to 4)
	 */
	public int getNumColumns() {
		return numColumns;
	}

	/**
	 * @param numColumns the number of columns (1 to 4) to set — resets hole selection
	 */
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
		resetSelectedHoles();
	}

	/**
	 * @return the number of rows (1 or 2)
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @param numRows the number of rows (1 or 2) to set — resets hole selection
	 */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
		resetSelectedHoles();
	}

	/**
	 * @return the height multiplier (1 or 2), only relevant for Access Hole
	 */
	public int getHeightMultiplier() {
		return heightMultiplier;
	}

	/**
	 * @param heightMultiplier the height multiplier (1 or 2) to set
	 */
	public void setHeightMultiplier(int heightMultiplier) {
		this.heightMultiplier = heightMultiplier;
	}

	public boolean isMakeDivot() {
		return makeDivot;
	}

	public void setMakeDivot(boolean makeDivot) {
		this.makeDivot = makeDivot;
	}

	public boolean isUseOffset() {
		return useOffset;
	}

	public void setUseOffset(boolean useOffset) {
		this.useOffset = useOffset;
	}

	/**
	 * @return 2×4 grid: selectedHoles[row][col], both zero-indexed
	 */
	public boolean[][] getSelectedHoles() {
		return selectedHoles;
	}

	public void setHoleSelected(int row, int col, boolean selected) {
		selectedHoles[row][col] = selected;
	}

	/**
	 * Resets all in-scope holes to selected. Called automatically when numColumns or numRows changes.
	 * Cells outside the current grid are left false.
	 */
	public void resetSelectedHoles() {
		boolean[][] h = new boolean[2][4];
		int rows = Math.max(numRows, 1);
		int cols = Math.max(numColumns, 1);
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				h[r][c] = true;
		selectedHoles = h;
	}

	private static boolean[][] allSelected() {
		boolean[][] h = new boolean[2][4];
		for (boolean[] row : h) Arrays.fill(row, true);
		return h;
	}

}
