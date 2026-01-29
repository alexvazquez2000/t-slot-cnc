package com.t_slot_cnc.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class Combo extends Counterbore {
	/*
        <counterbore>
            <diameter>0.563</diameter>
            <yOffset>0.406</yOffset>
            <depth>0.425</depth>
            <image>/10-series/EX-1010-Counterbore.png</image>
        </counterbore>
        <combo>
            <diameter>0.257</diameter>
            <yOffset>0.5</yOffset><!-- style C has one hole -->
            <!-- style D has 2 holes at 0.5 and 1.5 -->
            <depth>0.328</depth>
            <flatBottom>0.406</flatBottom>
            <!-- The counterbore symbol is ⌴ (Unicode U+2334), used in technical drawings
             and engineering to represent a flat-bottomed, cylindrical enlargement of a hole,
              allowing fasteners to sit flush or below the surface. It is typically followed 
              by the counterbore diameter (emptyset) and depth (downarrow) -->
            <image>/10-series/EX-1010-ACCESS-HOLE-COUNTERBORE.png</image>
        </combo>
	 */
	private double flatBottom;

	/**
	 * @return the flatBottom
	 */
	@XmlElement
	public double getFlatBottom() {
		return flatBottom;
	}

	/**
	 * @param flatBottom the flatBottom to set
	 */
	public void setFlatBottom(double flatBottom) {
		this.flatBottom = flatBottom;
	}


}
