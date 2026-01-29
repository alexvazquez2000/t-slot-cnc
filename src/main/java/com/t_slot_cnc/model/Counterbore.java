package com.t_slot_cnc.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class Counterbore extends AccessHole {
	/*
	<counterbore>
            <diameter>0.563</diameter>
            <yOffset>0.406</yOffset>
            <depth>0.425</depth>
            <image>/10-series/EX-1010-Counterbore.png</image>
        </counterbore>
	 */

	private double depth;

	/**
	 * @return the depth
	 */
	@XmlElement
	public double getDepth() {
		return depth;
	}

	/**
	 * @param depth the depth to set
	 */
	public void setDepth(double depth) {
		this.depth = depth;
	}
	
}
