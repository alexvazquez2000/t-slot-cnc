package com.t_slot_cnc.model;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class SelectionModel {
	private String selectedSeries;
	private HoleType holeType;

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

}
