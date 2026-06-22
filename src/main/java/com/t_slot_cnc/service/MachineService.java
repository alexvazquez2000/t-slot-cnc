package com.t_slot_cnc.service;

import com.t_slot_cnc.model.MachineSettings;

/**
 * Cutting parameters for a single units system, derived from the values in
 * specs/machine.properties (see {@link MachineSettingsService}).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineService {

	private static final MachineSettingsService SETTINGS_SERVICE = new MachineSettingsService();

	//aluminum settings
	//30–50 IPM (inches per minute) feed rate, with shallow depths of cut ( ~0.01-0.03").
	//inches per minute
	private double endMillDiameter;
	private int feedRate;
	private int drillFeedRate;
	//recommended is betwee 10k and 24K.  Speed 7 is 18,000 RPMs
	//Speed is actually being ignored
	private int spindleSpeed;
	// Recommended Depth of Cut (DOC): 0.01" - 0.03" (10-30% of diameter)
	private double cutDepthPerPass;
	private double accuracy;

	//z-gap above material - inches
	private double zGapAbove;

	public MachineService(String units) {
		MachineSettings settings = SETTINGS_SERVICE.load();

		endMillDiameter = settings.getEndMillDiameter();
		feedRate = settings.getFeedRate();
		drillFeedRate = settings.getDrillFeedRate();
		spindleSpeed = settings.getSpindleSpeed();
		cutDepthPerPass = settings.getCutDepthPerPass();
		accuracy = settings.getAccuracy();
		zGapAbove = settings.getzGapAbove();

		if (units.equals("mm")) {
			endMillDiameter *= 25.4;
			//inches per minute to mm/minute
			feedRate *= 25.4;
			cutDepthPerPass *= 25.4;
			accuracy *= 25.4;
			zGapAbove *= 25.4;
			drillFeedRate *= 25.4;
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

	public double getDrillFeedRate() {
		return drillFeedRate;
	}

}
