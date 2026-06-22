package com.t_slot_cnc.model;

/**
 * Machine cutting parameters, in inches (the base unit machine.properties is stored in).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineSettings {

	private double endMillDiameter;
	private int feedRate;
	private int drillFeedRate;
	private int spindleSpeed;
	private double cutDepthPerPass;
	private double accuracy;
	private double zGapAbove;

	public double getEndMillDiameter() {
		return endMillDiameter;
	}

	public void setEndMillDiameter(double endMillDiameter) {
		this.endMillDiameter = endMillDiameter;
	}

	public int getFeedRate() {
		return feedRate;
	}

	public void setFeedRate(int feedRate) {
		this.feedRate = feedRate;
	}

	public int getDrillFeedRate() {
		return drillFeedRate;
	}

	public void setDrillFeedRate(int drillFeedRate) {
		this.drillFeedRate = drillFeedRate;
	}

	public int getSpindleSpeed() {
		return spindleSpeed;
	}

	public void setSpindleSpeed(int spindleSpeed) {
		this.spindleSpeed = spindleSpeed;
	}

	public double getCutDepthPerPass() {
		return cutDepthPerPass;
	}

	public void setCutDepthPerPass(double cutDepthPerPass) {
		this.cutDepthPerPass = cutDepthPerPass;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public double getzGapAbove() {
		return zGapAbove;
	}

	public void setzGapAbove(double zGapAbove) {
		this.zGapAbove = zGapAbove;
	}

}
