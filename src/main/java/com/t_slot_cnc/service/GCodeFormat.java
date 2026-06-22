package com.t_slot_cnc.service;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public final class GCodeFormat {

	private GCodeFormat() {
		//Don't instantiate - only static members
	}

	public static String format(double value, int decimals) {
		return String.format("%." + decimals + "f", value);
	}
}
