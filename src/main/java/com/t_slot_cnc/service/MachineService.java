package com.t_slot_cnc.service;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineService {
	private double endMillDiameter = 0.125;
	
	//aluminum settings
	//30–50 IPM (inches per minute) feed rate, with shallow depths of cut ( ~0.01-0.03").
	//inches per minute
	private int feedRate = 40;
	private int spindleSpeed = 7;
	private double cutDepthPerPass = 0.02;
	private double accuracy = 0.02;
	
	//z-gap above material
	private double zGapAbove = 0.2;

	public MachineService(String units) {
		
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
