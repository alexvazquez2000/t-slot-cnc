package com.t_slot_cnc.service;

import org.springframework.stereotype.Service;

/**
 * Builds the raw G-code moves: machine header/tail boilerplate and the
 * cutting toolpaths for counterbores, access holes, and drilled holes.
 *
 * G-code uses the commands G02 for clockwise (CW) and G03 for counter-clockwise (CCW) circular
 * interpolation. These commands define an arc from a start point (current position) to an
 * endpoint, specifying either the radius (R) or the center point's relative coordinates (I, J, K).
 *
 * G-code Program Example: Full Circle using I, J
 * This example program uses the I and J parameters, which are incremental distances from the arc
 * start point to the arc center point along the X and Y axes, respectively.
 *
 *     N10 G90 G21 G17; Set absolute positioning (G90), metric units (G21), XY plane selection (G17)
 *     N20 G00 X0 Y0 Z10; Rapid move to starting position above material
 *     N30 G01 Z-5 F100; Linear move down into the material (Z-axis)
 *     N40 G02 I10 J0 F200; Clockwise full circle with center 10mm in the X direction
 *     N50 G00 Z10; Rapid move up from material
 *     N60 M30; Program end and rewind
 *
 * Alternative Method: Using Radius (R) - typically used for arcs less than a full circle.
 *     N40 G03 X10 Y10 R10 F200; Counter-clockwise arc to X10 Y10 with radius 10
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class ToolpathService {

	/**
	 * @param units only valid values are either "inches" or "mm"
	 */
	public String header(String units, MachineService machine) {
		StringBuilder head = new StringBuilder();
		//command sequence used to initiate a tool change.
		//T1: Selects Tool Number 1.
		//M6: Executes the tool change command.
		//head.append("T1M6; ()").append("\n");

		if (units.equals("inches")) {
			//G20 Set units to inches  - G21 uses mm
			head.append("G20; (G20  inches - G21 uses mm)").append("\n");
		} else if (units.equals("mm")) {
			head.append("G21; (G20  inches - G21 uses mm)").append("\n");
		} else {
			throw new RuntimeException("Unsupported units='" + units + "'");
		}

		//G90 absolute mode -  G91 is relative positioning mode.
		head.append("G90; (Set positioning to absolute mode)").append("\n");

		//G17 is XY plane
		head.append("G17; (XY plane)").append("\n");

		//M3 turns on the spindle clockwise (CW)
		head.append("M3 S" + machine.getSpindleSpeed()).append("; (M3 turns spindle on, S=speed being ignored)\n");

		//z-gap above material
		head.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(),2)).append("; (gap above the material=" + machine.getzGapAbove() +")\n");
		//Go home and turn on the spindle
		head.append("G00 X0.0 Y0.0; (start to user home)").append("\n");

		return head.toString();
	}

	public String tail(MachineService machine) {
		StringBuilder tail = new StringBuilder();

		//z-gap above material
		tail.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(), 4)).append(" ; (Move to safe clearance)\n");
		//Go home
		tail.append("G00 X0 Y0 ; (Return to home or loading position)").append("\n");
		//tail.append("M05 ; (Spindle stop)").append("\n");
		//tail.append("G00 Z0 ; (Move down to zero)\n");

		/*G-code M30 signifies the end of a program, resets parameters,
		 *  and rewinds to the beginning, allowing for immediate restart,
		 *  unlike M02 which ends but leaves the machine state potentially
		 *  different. It's a common command for cycling programs on CNC
		 *  machines, turning off coolant, stopping the spindle, and
		 *  setting default conditions like absolute mode and XY plane selection. */
		//Turn off the spindle
		tail.append("M30 ; (End Program)").append("\n");
		return tail.toString();
	}

	public String counterbore(MachineService machine, double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfBore) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = machine.getEndMillDiameter() / 2.0;
		//Start relative to the top of the Z=0 - the Z-axis moves down into negative numbers
		double z = 0.0;

		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;
		double radius = (boreDiameter / 2.0) - endMillRadius;

		//go initial location on middle of track, away from the home
		path.append("G01 X").append(GCodeFormat.format(centerX,4))
		.append(" Y").append(GCodeFormat.format(centerY + radius,4))

		.append(" Z").append(GCodeFormat.format(z,4))
		.append(" F").append(GCodeFormat.format(machine.getFeedRate(),1)).append("\n");

		while (z > -depthOfBore) {
			//J=Y-offset and I=X-offset
			path.append("G02 I0")
			.append(" J").append(GCodeFormat.format(-radius,4))
			.append(" Z").append(GCodeFormat.format(z,4))
			.append("\n");

			z -= machine.getCutDepthPerPass();
		}
		//do a final spiral to the exact depth
		z = -depthOfBore;
		path.append("G02 I0")
		.append(" J").append(GCodeFormat.format(-radius,4))
		.append(" Z").append(GCodeFormat.format( -depthOfBore,4))
		.append("; (Final spiral down to target depth)\n");

		//Clear the bottom 
		for(double rr=radius; rr > machine.getEndMillDiameter()/2; rr -= machine.getAccuracy()) {
			//Counter clockwise full circle with center 10mm in the X direction
			//G02 I-1.0 J0.0 F8.0; (Clockwise full circle with a center 1 inch in the negative X direction from the start point)
			//path.append("G01 Y").append(GCodeFormat.format(centerY + rr,4)).append("\n");
			//path.append("G03 I0").append(" J").append(GCodeFormat.format(-rr,4)).append("\n");
			path.append(makeCircleAt(centerX, centerY, rr, machine.getAccuracy())).append("; (circle)\n");
		}

		return path.toString();
	}

	private static Object makeCircleAt(double centerX, double centerY, double radius, double accuracy) {
		StringBuilder path = new StringBuilder();

		// Number of points to generate - circumference divided by the accuracy
		final int numPoints = (int)((2 * radius * Math.PI ) / accuracy);
		//path.append("; number of points = " + numPoints).append("\n"); 

		for (int i = 0; i < numPoints; ++i) {
			// Calculate angle in radians
			double angle = Math.toRadians(((double) i / numPoints) * 360d + 90d);

			// Calculate coordinates
			double x = centerX + radius * Math.cos(angle);
			double y = centerY + radius * Math.sin(angle);
			path.append("G01 X").append(GCodeFormat.format(x,4))
				.append(" Y").append(GCodeFormat.format(y,4))
				.append("\n");
		}

		//path.append("; circle done").append("\n"); 

		return path;
	}

	public String accessHole(MachineService machine, double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfAccessHole, double startZ) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = machine.getEndMillDiameter() / 2.0;
		double z = - startZ - machine.getCutDepthPerPass();

		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;

		double radius = boreDiameter / 2 - endMillRadius;

		//go initial location on middle of track, away from the home
		path.append("G01 X").append(GCodeFormat.format(centerX,4))
		.append(" Y").append(GCodeFormat.format(centerY + radius,4))
		.append(" Z").append(GCodeFormat.format(z,4))
		.append(" F").append(GCodeFormat.format(machine.getFeedRate(),1)).append("\n");

		while (z > -depthOfAccessHole) {

			//J=Y-offset and I=X-offset
			path.append("G03 I0")
			.append(" J").append(GCodeFormat.format(-radius,4))
			.append(" Z").append(GCodeFormat.format(z,4))
			.append("\n");

			z -= machine.getCutDepthPerPass();
		}
		//do a final spiral to the exact depth
		path.append("G03 I0")
		.append(" J").append(GCodeFormat.format(-radius,4))
		.append(" Z").append(GCodeFormat.format( -depthOfAccessHole,4))
		.append("\n");

		//This is a through hole, no need to do a last cut at the bottom - but this would work
		//path.append("G03 I0").append(" J").append(format(-radius,4)).append("\n")

		return path.toString();
	}

	public String drillHole(MachineService machine, double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfAccessHole) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = 0.25 / 2.0;
		//the final depth
		double z = - depthOfAccessHole + machine.getCutDepthPerPass();

		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;

		//go initial location on middle of track, away from the home
		path.append("G01 X").append(GCodeFormat.format(centerX,4))
		.append(" Y").append(GCodeFormat.format(centerY,4))
		.append(" F" + machine.getDrillFeedRate() + "\n");

		path.append("G01 Z").append(GCodeFormat.format(z, 4)).append("\n");
		//This is a through hole, no need to do a last cut at the bottom

		return path.toString();
	}

	/**
	 * Relative move from current location - assumes it is at home
	 *
	 * @param x in millimeters
	 * @param y in millimeters
	 */
	public String generateReturnToVice(double x, double y) {
		StringBuilder head = new StringBuilder();
		//G20 Set units to inches  - G21 uses mm
		head.append("G21; (G20  inches - G21 uses mm the machine uses mm)").append("\n");
		//G90 absolute mode -  G91 is relative positioning mode.
		head.append("G91; (Set positioning to relative mode)").append("\n");

		//G17 is XY plane
		head.append("G17; (XY plane)").append("\n");

		//M3 turns on the spindle clockwise (CW)
		head.append("M3 S0; (NX-105 ignores the spindle speed)").append("\n");

		//z-gap above material
		//Homing: G28 tells the machine to move to coordinate in machine space.
		head.append("G28 Z0; (Raises Z to home first)").append("\n");
		head.append("G28 X0 Y0; (Go home for X and Y)").append("\n");

		//Go home and turn on the spindle
		head.append("G00 X" + GCodeFormat.format(x,3) + "Y" + GCodeFormat.format(y,3) + "; (Move towards the vice in mm - get this while at the vice)").append("\n");
		//M30 is end of script- It turns off the spindle
		head.append("M30; (End script - turns off the spindle)").append("\n");
		return head.toString();
	}
}
