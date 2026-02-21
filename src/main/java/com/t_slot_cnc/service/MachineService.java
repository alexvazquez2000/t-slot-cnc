package com.t_slot_cnc.service;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineService {
	//private double endMillDiameter = 0.125;
	private double endMillDiameter = 0.25;
	
	//aluminum settings
	//30–50 IPM (inches per minute) feed rate, with shallow depths of cut ( ~0.01-0.03").
	//inches per minute
	private int feedRate = 40;
	//recommended is betwee 10k and 24K.  Speed 7 is 18,000 RPMs
	//Speed is actually being ignored
	private int spindleSpeed = 7; 
	// Recommended Depth of Cut (DOC): 0.01" - 0.03" (10-30% of diameter)
	private double cutDepthPerPass = 0.02;
	private double accuracy = 0.02;
	
	//z-gap above material - inches
	private double zGapAbove = 0.2;

	public MachineService(String units) {
		if (endMillDiameter == 0.25) {
			//inches per minute to mm/minute
			feedRate = 35;
			cutDepthPerPass = 0.04;
			accuracy = 0.02;
			zGapAbove = 0.03;
		}
		
		if (units.equals("mm")) {
			endMillDiameter *= 25.4;
			//inches per minute to mm/minute
			feedRate *= 25.4;
			cutDepthPerPass *= 25.4;
			accuracy *= 25.4;
			zGapAbove *= 25.4;
		}
	}

	/**
	 * @return the endMillDiameter
	 */
	public double getEndMillDiameter() {
		return endMillDiameter;
	}

	/**
	 * @return the feedRate
	 */
	public int getFeedRate() {
		return feedRate;
	}

	/**
	 * @return the spindleSpeed
	 */
	public int getSpindleSpeed() {
		return spindleSpeed;
	}

	/**
	 * @return the cutDepthPerPass
	 */
	public double getCutDepthPerPass() {
		return cutDepthPerPass;
	}

	/**
	 * @return the accuracy
	 */
	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @return the zGapAbove
	 */
	public double getzGapAbove() {
		return zGapAbove;
	}

}
