package com.t_slot_cnc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	static double endMillDiameter = 0.125;
	
	//aluminum settings
	//30–50 IPM (inches per minute) feed rate, with shallow depths of cut ( ~0.01-0.03").
	//inches per minute
	static int feedRate = 30;
	static int spindleSpeed = 2000;
	static double cutDepthPerPass = 0.02;
	static double accuracy = 0.02;
	
	//z-gap above material
	static double zGapAbove = 0.2;

	//These are settings for the t-track
	static double slotWidth = 0.5;
	double depthToTopOCenter = 0.323;
	

	public static void main(String[] args) throws IOException {
		//10-series/EX-1010-Counterbore.png
		double boreLocationX = 0.5;
		double boreLocationY = 0.406;
		double boreDiameter = 0.563;
		double depthOfBore = 0.425;
		
		String gCode = generateCounterbore(boreLocationX, boreLocationY, boreDiameter, depthOfBore);
		saveGCode(gCode, "1010_counterbore.txt");

		//<diameter>0.218</diameter>
		//<yOffset>0.5</yOffset>
		boreLocationX = 0.5;
		boreLocationY = 0.5;
		double accessHoleDiameter = 0.218;
		double centeOf1010 = 0.354; //from center-bottom of 10-series/EX-1010-details.jpg
		double topOfSlot = 0.31;  //1.0 - ((1.0 - centeOf1010) /2.0);
		double depthOfAccessHole = topOfSlot + centeOf1010 + cutDepthPerPass;
		gCode = generateAccessHole(boreLocationX, boreLocationY, accessHoleDiameter, depthOfAccessHole, topOfSlot);
		saveGCode(gCode, "1010_accesshole.txt");

		gCode = generateCombo(boreLocationX, boreLocationY,
				boreDiameter, depthOfBore,
				accessHoleDiameter, depthOfAccessHole);
		saveGCode(gCode, "1010_combo.txt");

	}

	private static void saveGCode(String gCode, String fileName) throws IOException {
		try {
			Path file = Paths.get(fileName);
			// This will create a new file or overwrite an existing one
			Files.writeString(file, gCode); 
			System.out.println("Successfully wrote the string to the file.");

		} catch (IOException e) {
			System.err.println("An error occurred: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	private static String generateCounterbore( double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfBore) {
		StringBuilder response = new StringBuilder();
		response.append(header(spindleSpeed));
		response.append(counterbore(boreLocationX, boreLocationY, boreDiameter, depthOfBore));
		response.append(tail());
		System.out.println(response.toString());
		return response.toString();
	}

	private static String generateAccessHole( double boreLocationX, double boreLocationY,
			double accessHoleDiameter, double depthOfAccessHole, double zStart) {
		StringBuilder response = new StringBuilder();
		response.append(header(spindleSpeed));
		response.append(accessHole(boreLocationX, boreLocationY, accessHoleDiameter, depthOfAccessHole, zStart));
		response.append(tail());
		System.out.println(response.toString());
		return response.toString();
	}

	private static String generateCombo( double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfBore,
			double accessHoleDiameter, double depthOfAccessHole) {
		StringBuilder response = new StringBuilder();
		response.append(header(spindleSpeed));
		response.append(counterbore(boreLocationX, boreLocationY, boreDiameter, depthOfBore));
		response.append("; end counterbore, start accessHole");
		
		//TODO: Clear bottom of the counterbore
		//move to material surface until we clear the bottom of the counterbore
		response.append("G00 Z" + format(0.0,4)).append("\n");
				
		response.append(accessHole(boreLocationX, boreLocationY, accessHoleDiameter, depthOfAccessHole, depthOfBore));
		response.append(tail());
		System.out.println(response.toString());
		return response.toString();
	}

	
	private static String header(int spindleSpeed) {
		StringBuilder head = new StringBuilder();
		//command sequence used to initiate a tool change.
		//T1: Selects Tool Number 1.
		//M6: Executes the tool change command. 
		//head.append("T1M6").append("\n");
		//G90 can set the 
		//G20 Set units to inches  - G21 uses mm 
		head.append("G20").append("\n");
		
		head.append("G90; (Set positioning to absolute mode)").append("\n");

		//G17 is XY plane
		head.append("G17").append("\n");
		
		//M3 turns on the spindle clockwise (CW)
		head.append("M3 S" + spindleSpeed).append("\n");
		
		//z-gap above material
		head.append("G00 Z" + format(zGapAbove,1) + " F10.0").append("\n");
		//Go home and turn on the spindle
		head.append("G00 X0.0 Y0.0 F10.0").append("\n");
		//head.append("; prolog completed").append("\n");
		return head.toString();
	}

	private static String format(double value, int decimals) {
		return String.format("%." + decimals + "f", value);
	}

	private static String counterbore( double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfBore) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = endMillDiameter / 2.0;
		//Start relative to the top of the Z=0 - the Z-axis moves down into negative numbers 
		double z = - cutDepthPerPass;
		
		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;
		//go initial location on middle of track, away from the home
		path.append("G01 X").append(format(centerX,4))
			.append(" Y").append(format(centerY,4))
			.append(" F").append(format(feedRate,1)).append("\n");
		
		//path.append("G01 Z").append(format(0.0,4));
		//path.append(makeCircleAt(centerX, centerY, boreDiameter/2.0));
		double radius = (boreDiameter / 2.0) - endMillRadius;
		while (z > -depthOfBore) {
			//move to the level
			path.append("G01 Z").append(format(z,4))
				.append(" F").append(format(feedRate,1)).append("\n"); //; Linear move down into the material (Z-axis)
			
			path.append(makeCircleAt(centerX, centerY, radius));
			z -= cutDepthPerPass;
		}
		//do a final pass
		path.append("G01 X").append(format(centerX + radius,4))
			.append(" Y").append(format(centerY,4))
			.append(" Z").append(format(-depthOfBore,4))
			.append("\n");

		//move to the level
		path.append("G01 Z").append(format(z,4)).append("\n"); //; Linear move down into the material (Z-axis)
		for(double rr=radius; rr > cutDepthPerPass; rr-=cutDepthPerPass) {
			//Counter clockwise full circle with center 10mm in the X direction
			//G02 I-1.0 J0.0 F8.0; (Clockwise full circle with a center 1 inch in the negative X direction from the start point)
			path.append("G01 X").append(format(centerX + rr,4)).append("\n");

			path.append("G02 I").append(format(-rr,4)).append(" J0").append("\n");
		}
		return path.toString();
	}


	private static String accessHole( double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfAccessHole, double startZ) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = endMillDiameter / 2.0;
		double z = - startZ - cutDepthPerPass;
		
		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;
		//go initial location on middle of track, away from the home
		path.append("G01 X").append(format(centerX,4))
			.append(" Y").append(format(centerY,4))
			.append(" F").append(format(feedRate,1)).append("\n");
		
		//path.append("G01 Z").append(format(0.0,4));
		//path.append(makeCircleAt(centerX, centerY, boreDiameter/2.0));

		while (z > -depthOfAccessHole) {
			//move to the level
			path.append("G01 Z").append(format(z,4))
				.append(" F").append(format(feedRate,1)).append("\n"); //; Linear move down into the material (Z-axis)
			
			double radius = boreDiameter / 2 - endMillRadius;
			path.append(makeCircleAt(centerX, centerY, radius));
			//go down
			z -= cutDepthPerPass;
		}
		return path.toString();
	}


	private static Object makeCircleAt(double centerX, double centerY, double radius) {
		StringBuilder path = new StringBuilder();

		// Number of points to generate - circumference divided by the accuracy
		final int numPoints = (int)((2 * radius * Math.PI ) / accuracy);
		//path.append("; number of points = " + numPoints).append("\n"); 

		for (int i = 0; i < numPoints; ++i) {
			// Calculate angle in radians
			double angle = Math.toRadians(((double) i / numPoints) * 360d);

			// Calculate coordinates
			double x = centerX + radius * Math.cos(angle);
			double y = centerY + radius * Math.sin(angle);
			path.append("G01 X").append(format(x,4))
				.append(" Y").append(format(y,4))
				.append("\n");
		}
		
		//path.append("; circle done").append("\n"); 

		return path;
	}

	/*
G-code uses the commands
G02 for clockwise (CW) and G03 for counter-clockwise (CCW) circular interpolation. These commands define
 an arc from a start point (current position) to an endpoint, specifying either the radius (R) or the center point's relative coordinates (I, J, K). 
G-code Program Example: Full Circle using I, J
This example program uses the I and J parameters, which are incremental distances from the arc start point to the arc center point along the X and Y axes, respectively.

			N10 G90 G21 G17; Set absolute positioning (G90), metric units (G21), XY plane selection (G17)
			N20 G00 X0 Y0 Z10; Rapid move to starting position above material
			N30 G01 Z-5 F100; Linear move down into the material (Z-axis)
			N40 G02 I10 J0 F200; Clockwise full circle with center 10mm in the X direction
			N50 G00 Z10; Rapid move up from material
			N60 M30; Program end and rewind

Explanation:

    N10: Sets the machine to use absolute coordinates (G90), metric units (G21), and the XY plane (G17).
    N20: Moves the tool rapidly (G00) to the starting point X0, Y0, Z10.
    N30: Feeds the tool (G01) down to Z-5 at a feed rate of 100 mm/min.
    N40: Executes the circular interpolation (G02).
        The start point and end point are the same (X0 Y0) in this line, which tells the machine to complete a full circle.
        I10 J0 defines the center point as 10mm in the positive X direction and 0mm in the Y direction relative to the start point (0, 0).
        F200 sets the feed rate for the arc move.
    N50: Retracts the tool rapidly.
    N60: Ends the program (M30).
    
    
 Alternative Method: Using Radius (R)
You can also define an arc using a radius value R. This is typically used for arcs less than a full circle. 
gcode

N10 G90 G21 G17; Set absolute positioning, metric units, XY plane
N20 G00 X0 Y0 Z10; Rapid move to starting position
N30 G01 Z-5 F100; Feed into material
N40 G03 X10 Y10 R10 F200; Counter-clockwise arc to X10 Y10 with radius 10
N50 G00 Z10; Rapid move up
N60 M30; Program end

    N40: G03 initiates a counter-clockwise arc to the endpoint X10 Y10 with a radius of R10. The control automatically determines the center point based on the start point, end point, and radius value.


	 */
	private static String tail() {
		StringBuilder tail = new StringBuilder();
		//tail.append("; path completed").append("\n");
		//z-gap above material
		tail.append("G0 Z" + format(zGapAbove,4)).append("\n");
		//Go home
		tail.append("G0 X0.0000 Y0.0000").append("\n");
		
		//M05; (Spindle off)

		/*G-code M30 signifies the end of a program, resets parameters,
		 *  and rewinds to the beginning, allowing for immediate restart,
		 *  unlike M02 which ends but leaves the machine state potentially
		 *  different. It's a common command for cycling programs on CNC
		 *  machines, turning off coolant, stopping the spindle, and
		 *  setting default conditions like absolute mode and XY plane selection. */
		//Turn off the spindle
		tail.append("M30").append("\n");
		return tail.toString();
	}


}
