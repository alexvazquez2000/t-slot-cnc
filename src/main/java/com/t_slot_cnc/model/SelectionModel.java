package com.t_slot_cnc.model;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class SelectionModel {
	private String selectedSeries;
	private String selectedHoleType;

	public String getSelectedSeries() {
		return selectedSeries;
	}

	public void setSelectedSeries(String selectedSeries) {
		this.selectedSeries = selectedSeries;
	}

	/**
	 * @return the selectedHoleType
	 */
	public String getSelectedHoleType() {
		return selectedHoleType;
	}

	/**
	 * @param selectedHoleType the selectedHoleType to set
	 */
	public void setSelectedHoleType(String selectedHoleType) {
		this.selectedHoleType = selectedHoleType;
	}

}
