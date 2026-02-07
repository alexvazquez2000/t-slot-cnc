package com.t_slot_cnc.model;

import jakarta.xml.bind.annotation.XmlElement;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class AccessHole {
	/*
        <accessHole>
            <partNumber>M70511</partNumber>
            <diameter>0.218</diameter>
            <yOffset>0.5</yOffset>
            <image>/10-series/EX-1010-ACCESS-HOLE.png</image>
        </accessHole>

	 */
	
	private String partNumber;
	private double diameter;
	private double yOffset;
	private String image;
	
	/**
	 * @return the partNumber
	 */
	public String getPartNumber() {
		return partNumber;
	}
	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	/**
	 * @return the diameter
	 */
	@XmlElement
	public double getDiameter() {
		return diameter;
	}
	/**
	 * @param diameter the diameter to set
	 */
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}
	/**
	 * @return the yOffset
	 */
	@XmlElement
	public double getyOffset() {
		return yOffset;
	}
	/**
	 * @param yOffset the yOffset to set
	 */
	public void setyOffset(double yOffset) {
		this.yOffset = yOffset;
	}
	/**
	 * @return the image
	 */
	@XmlElement
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}
	
}
