package com.t_slot_cnc.model;

public enum HoleType {
	ACCESS_HOLE ("Access Hole"),
	COUNTERBORE("Counterbore");
	
	String name;
	
	HoleType(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
