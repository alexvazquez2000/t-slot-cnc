package com.t_slot_cnc.model;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class SelectionModel {
	private String selectedSeries;
	private HoleType holeType;
	private int numColumns;
	private int numRows;
	private int heightMultiplier;

	public String getSelectedSeries() {
		return selectedSeries;
	}

	public void setSelectedSeries(String selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	/**
	 * @return the selectedHoleType
	 */
	public HoleType getHoleType() {
		return holeType;
	}

	/**
	 * @param selectedHoleType the selectedHoleType to set
	 */
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
	 * @param numColumns the number of columns (1 to 4) to set
	 */
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	/**
	 * @return the number of rows (1 or 2)
	 */
	public int getNumRows() {
		return numRows;
	}

	/**
	 * @param numRows the number of rows (1 or 2) to set
	 */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
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

}
