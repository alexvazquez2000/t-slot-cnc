package com.t_slot_cnc.model;

import jakarta.xml.bind.annotation.XmlElement;

public class Extrusion {
	private String id;

	private String units;
	private double width;
	
	private AccessHole accessHole;
	private Counterbore counterbore;
	
	/**
	 * @return the id
	 */
	@XmlElement
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the accessHole
	 */
	@XmlElement
	public AccessHole getAccessHole() {
		return accessHole;
	}

	/**
	 * @param accessHole the accessHole to set
	 */
	public void setAccessHole(AccessHole accessHole) {
		this.accessHole = accessHole;
	}

	/**
	 * @return the counterbore
	 */
	@XmlElement
	public Counterbore getCounterbore() {
		return counterbore;
	}

	/**
	 * @param counterbore the counterbore to set
	 */
	public void setCounterbore(Counterbore counterbore) {
		this.counterbore = counterbore;
	}

}

/*
    <extrusion>
        <id>1010</id>
        <accessHole>
            <diameter>0.218</diameter>
            <yOffset>0.5</yOffset><!-- style C has one hole -->
            <!-- style D has 2 holes at 0.5 and 1.5 -->
            <image>/10-series/EX-1010-ACCESS-HOLE.png</image>
        </accessHole>
        <counterbore>
            <diameter>0.563</diameter>
            <yOffset>0.406</yOffset>
            <depth>0.425</depth>
            <image>/10-series/EX-1010-Counterbore.png</image>
        </counterbore>
    </extrusion>
     
 */
