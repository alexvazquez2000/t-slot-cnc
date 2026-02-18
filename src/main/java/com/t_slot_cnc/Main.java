package com.t_slot_cnc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.service.ExtrusionsService;
import com.t_slot_cnc.service.FileNameService;
import com.t_slot_cnc.service.MachineService;

public class Main {

	//These are settings for the t-track
	static double slotWidth = 0.5;
	double depthToTopOCenter = 0.323;

	static List<String> outFileList = new ArrayList<>();


	public static void main(String[] args) throws IOException {
		String gCode;

		ExtrusionsService service = new ExtrusionsService();
		service.loadSpecs();

		//Generate all the files
		for (Extrusion ext : service.getExtrusions().getExtrusionSeries()) {

			if (ext.getCounterbore() != null) {

				generateCounterbore(ext, new int[] {0});

				generateCounterbore(ext, new int[] {0,1});

				generateCounterbore(ext, new int[] {0,1,2});

				generateCounterbore(ext, new int[] {0,1,2,3});
			}


			AccessHole accessHole = ext.getAccessHole();
			for (int rows = 1; rows <=2; rows++) {
				
				generateDrillHole(ext, new int[] {0}, rows, 1);

				generateDrillHole(ext, new int[] {0}, rows, 2);

				generateDrillHole(ext, new int[] {0,1}, rows, 1);

				generateDrillHole(ext, new int[] {0,1}, rows, 2);

				generateDrillHole(ext, new int[] {0,1,2}, rows, 1);

				generateDrillHole(ext, new int[] {0,1,2}, rows, 2);

				generateDrillHole(ext, new int[] {0,1,2,3}, rows, 1);

				generateDrillHole(ext, new int[] {0,1,2,3}, rows, 2);


				//Access holes
				generateAccessHole(ext, new int[] {0}, rows);

				generateAccessHole(ext, new int[] {0,1}, rows);

				generateAccessHole(ext, new int[] {0,1,2}, rows);

				generateAccessHole(ext, new int[] {0,1,2,3}, rows);
			}
		}

		StringBuilder parts = new StringBuilder();
		for (String line : outFileList) {
			System.out.println(line);
			parts.append(line).append("\n");
		}
		saveGCode(parts.toString(), "output/parts.txt");

		//return to origin - params in millimeters
		gCode = generateReturnVice(124.245, 68.406);
		saveGCode(gCode, "returnToVice.txt");

	}

	private static String partDesc(Extrusion ext, String fileName, MachineService machine) {
		StringBuilder part = new StringBuilder();
		part.append(ext.getId()).append("\t")
		.append(fileName.substring(fileName.lastIndexOf("/")+1)).append("\t")
		.append(ext.getUnits()).append("\t");

		if (fileName.contains("_cb_")) {
			Counterbore counterbore = ext.getCounterbore();
			if (counterbore != null) {
				part.append("Counterbore").append("\t")
				.append(counterbore.getyOffset()).append("\t")
				.append(ext.getWidth()/2.0).append("\t")
				.append(counterbore.getDiameter() + " x " + counterbore.getDepth() + " deep").append("\t")
				.append(ext.getWidth());
				if (ext.getUnits().equals("mm")) {
					part.append("\n-\t-\t-\t-\t")
					.append(format(counterbore.getyOffset() /25.4, 3)).append("\"\t")
					.append(format((ext.getWidth()/2.0) /25.4, 3)).append("\"\t")
					.append(format((counterbore.getDiameter() /25.4), 3) 
							+ "\" x " + format(counterbore.getDepth()/25.4, 3) + "\" deep").append("\t")
					.append(ext.getWidth());
				}
			}
		} else {
			double topOfSlot = ext.getDepthToTopOfSlot();
			double coreWidth = ext.getWidth() - 2*topOfSlot;
			//maybe it should be in the specs, instead of calculating it here
			double depthOfAccessHole = topOfSlot + coreWidth + machine.getCutDepthPerPass();

			AccessHole accessHole = ext.getAccessHole();
			part.append("Access Hole").append("\t")
			.append(accessHole.getyOffset()).append("\t")
			.append(ext.getWidth()/2.0).append("\t")
			.append(accessHole.getDiameter() + " x through " + depthOfAccessHole + " deep").append("\t")
			.append(ext.getWidth()).append("\t");
		}

		return part.toString();
	}

	/**
	 * Relative move from current location - assumes it is at home
	 * 
	 * @param x in millimeters
	 * @param y in millimeters
	 * @return
	 */
	private static String generateReturnVice(double x, double y) {
		StringBuilder head = new StringBuilder();
		//G20 Set units to inches  - G21 uses mm 
		head.append("G21").append("\n");
		//G90 absolute mode -  G91 is relative positioning mode. 		
		head.append("G91; (Set positioning to relative mode)").append("\n");

		//G17 is XY plane
		head.append("G17").append("\n");

		//M3 turns on the spindle clockwise (CW)
		head.append("M3 S0; NX-105 ignores the spindle speed").append("\n");

		//z-gap above material
		//Homing: G28 tells the machine to move to coordinate in machine space.
		head.append("G28 Z0; (Raises Z to home first)").append("\n");
		head.append("G28 X0 Y0; (Go home for X and Y)").append("\n");

		//Go home and turn on the spindle
		head.append("G00 X" + format(x,3) + "Y" + format(y,3) + "; Move towards the vice in mm").append("\n");
		//M30 is end of script- It turns off the spindle
		head.append("M30").append("\n");
		return head.toString();
	}

	private static void saveGCode(String gCode, String fileName) throws IOException {
		try {
			Path file = Paths.get(fileName);
			
			//Make sure the directory exists
			File parentDir = file.toFile().getParentFile();
			if (parentDir!= null && !parentDir.exists()) {
				parentDir.mkdirs();
			}

			// This will create a new file or overwrite an existing one
			Files.writeString(file, gCode); 
			System.out.println("Successfully wrote the string to the file " + file.toString());

		} catch (IOException e) {
			throw e;
		}
	}

	private static String generateCounterbore(Extrusion ext, int[] pattern) throws IOException {
		StringBuilder response = new StringBuilder();

		MachineService machine = new MachineService(ext.getUnits());
		Counterbore counterbore = ext.getCounterbore();

		String fileName = FileNameService.nameCounterbore(ext, counterbore, pattern.length); 
	
		
		if (counterbore != null) {
			double boreLocationX = ext.getWidth() / 2.0;
			double boreLocationY = counterbore.getyOffset();
			double boreDiameter = counterbore.getDiameter();
			double depthOfBore = counterbore.getDepth();

			response.append(header(ext.getUnits(), machine));

			outFileList.add(partDesc(ext, fileName, machine));

			for (int p :pattern) {
				response.append(counterbore(ext, machine, boreLocationX + (p *ext.getWidth()) , boreLocationY, boreDiameter, depthOfBore));
				response.append("G00 Z" + format(machine.getzGapAbove(), 4)).append("\n");
			}
			response.append(tail(machine));
		}
		saveGCode(response.toString(), fileName);
		return response.toString();
	}

	private static String generateAccessHole( Extrusion ext, int[] pattern, int rows) throws IOException {
		StringBuilder response = new StringBuilder();

		MachineService machine = new MachineService(ext.getUnits());

		AccessHole accessHole = ext.getAccessHole();
		double boreLocationX = ext.getWidth() / 2.0;
		double boreLocationY = accessHole.getyOffset();
		double accessHoleDiameter = accessHole.getDiameter();
		double topOfSlot = ext.getDepthToTopOfSlot();
		double coreWidth = ext.getWidth() - 2*topOfSlot;
		//maybe it should be in the specs, instead of calculating it here
		double depthOfAccessHole = topOfSlot + coreWidth + machine.getCutDepthPerPass();

		String fileName = FileNameService.nameAccessHole(ext, accessHole, pattern.length, rows);
		outFileList.add(partDesc(ext, fileName, machine));

		response.append(header(ext.getUnits(), machine));
		for (int row=0; row < rows; row++) {
			for (int p :pattern) {
				response.append("G00 X").append(format(boreLocationX +(p *ext.getWidth()), 4))
				.append(" Y").append(format(boreLocationY + (row * ext.getWidth()), 4))
				.append("\n");
				response.append(accessHole(machine, boreLocationX + (p *ext.getWidth()),
						boreLocationY + (row * ext.getWidth()), accessHoleDiameter, depthOfAccessHole, topOfSlot));
				response.append("G00 Z").append(format(machine.getzGapAbove(), 4)).append("\n");
			}
		}
		response.append(tail(machine));

		saveGCode(response.toString(), fileName);
		return response.toString();
	}

	private static String generateDrillHole(Extrusion ext, int[] pattern, int rows, int multipier) throws IOException {
		StringBuilder response = new StringBuilder();

		MachineService machine = new MachineService(ext.getUnits());

		AccessHole accessHole = ext.getAccessHole();
		double boreLocationX = ext.getWidth() / 2.0;
		double boreLocationY = accessHole.getyOffset();
		double accessHoleDiameter = accessHole.getDiameter();
		
		//maybe it should be in the specs, instead of calculating it here
		double depthOfAccessHole;
		if (multipier == 1) {
			depthOfAccessHole = (ext.getWidth() * 0.8)  + machine.getCutDepthPerPass();
		} else if (multipier == 2) {
			depthOfAccessHole = ext.getWidth() + (ext.getWidth() * 0.8)  + machine.getCutDepthPerPass();
		} else {
			throw new RuntimeException("multiplier can only be 1 or 2");
		}

		String fileName = FileNameService.nameDrillHole(ext, pattern.length, rows, multipier);

		outFileList.add(partDesc(ext, fileName, machine));

		response.append(header(ext.getUnits(), machine));
		for (int row=0; row < rows; row++) {
			for (int p :pattern) {
				response.append("G00 X").append(format(boreLocationX +(p *ext.getWidth()), 4))
				.append(" Y").append(format(boreLocationY + (row * ext.getWidth()), 4))
				.append("\n");
				response.append(drillHole(machine, boreLocationX + (p *ext.getWidth()),
						boreLocationY + (row * ext.getWidth()), accessHoleDiameter, depthOfAccessHole));
				response.append("G00 Z").append(format(machine.getzGapAbove(), 4)).append("\n");
			}
		}
		response.append(tail(machine));

		saveGCode(response.toString(), fileName);
		return response.toString();
	}


	/**
	 * @param units only valid values are either "inches" or "mm"
	 * @param spindleSpeed - it is being ignored by
	 * @return
	 */
	private static String header(String units, MachineService machine) {
		StringBuilder head = new StringBuilder();
		//command sequence used to initiate a tool change.
		//T1: Selects Tool Number 1.
		//M6: Executes the tool change command. 
		//head.append("T1M6").append("\n");

		if (units.equals("inches")) {
			//G20 Set units to inches  - G21 uses mm 
			head.append("G20").append("\n");
		} else if (units.equals("mm")) {
			head.append("G21").append("\n");
		} else {
			throw new RuntimeException("Unsupported units='" + units + "'");
		}

		//G90 absolute mode -  G91 is relative positioning mode. 		
		head.append("G90; (Set positioning to absolute mode)").append("\n");

		//G17 is XY plane
		head.append("G17").append("\n");

		//M3 turns on the spindle clockwise (CW)
		head.append("M3 S" + machine.getSpindleSpeed()).append("\n");

		//z-gap above material
		head.append("G00 Z" + format(machine.getzGapAbove(),1)).append("\n");
		//Go home and turn on the spindle
		head.append("G00 X0.0 Y0.0").append("\n");

		return head.toString();
	}

	private static String format(double value, int decimals) {
		return String.format("%." + decimals + "f", value);
	}

	private static String counterbore(Extrusion ext, MachineService machine, double boreLocationX, double boreLocationY,
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
		path.append("G01 X").append(format(centerX,4))
		.append(" Y").append(format(centerY + radius,4))

		.append(" Z").append(format(z,4))
		.append(" F").append(format(machine.getFeedRate(),1)).append("\n");

		while (z > -depthOfBore) {
			//J=Y-offset and I=X-offset
			path.append("G02 I0")
			.append(" J").append(format(-radius,4))
			.append(" Z").append(format(z,4))
			.append("\n");

			z -= machine.getCutDepthPerPass();
		}
		//do a final spiral to the exact depth
		z = -depthOfBore;
		path.append("G02 I0")
		.append(" J").append(format(-radius,4))
		.append(" Z").append(format( -depthOfBore,4))
		.append("\n");

	
		if (ext.getId().startsWith("15")) {
			//it is too thick, go up and do 3 passess at the floor
			z = -depthOfBore + (3 * machine.getCutDepthPerPass());
			path.append("G01 Z").append(format(z,4)).append("\n");
			while (round(z) >= -depthOfBore) {
				for(double rr=radius; rr > machine.getEndMillDiameter()/2; rr -= machine.getAccuracy()) {
					//Counter clockwise full circle with center 10mm in the X direction
					//G02 I-1.0 J0.0 F8.0; (Clockwise full circle with a center 1 inch in the negative X direction from the start point)
					path.append("G01 Y").append(format(centerY + rr,4))
					.append(" Z").append(format(z,4)).append("\n");
					
					path.append("G03 I0")
					.append(" J").append(format(-rr,4))
					.append("\n");
				}
				path.append("G01 Y").append(format(centerY + radius,4))
				.append("Z").append(format(z,4)).append("\n");
				z -= machine.getCutDepthPerPass();
			}
		} else {	
			for(double rr=radius; rr > machine.getEndMillDiameter()/2; rr -= machine.getAccuracy()) {
				//Counter clockwise full circle with center 10mm in the X direction
				//G02 I-1.0 J0.0 F8.0; (Clockwise full circle with a center 1 inch in the negative X direction from the start point)
				path.append("G01 Y").append(format(centerY + rr,4)).append("\n");
	
				path.append("G03 I0")
				.append(" J").append(format(-rr,4))
				.append("\n");
			}
		}

		return path.toString();
	}


	private static double round(double value) {
		double dd = Math.round(value * 1000.0) / 1000.0;
		System.out.println(value + " -->" + dd);
		return dd;
	}

	private static String accessHole(MachineService machine, double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfAccessHole, double startZ) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = machine.getEndMillDiameter() / 2.0;
		double z = - startZ - machine.getCutDepthPerPass();

		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;

		double radius = boreDiameter / 2 - endMillRadius;

		//go initial location on middle of track, away from the home
		path.append("G01 X").append(format(centerX,4))
		.append(" Y").append(format(centerY + radius,4))
		.append(" Z").append(format(z,4))
		.append(" F").append(format(machine.getFeedRate(),1)).append("\n");

		while (z > -depthOfAccessHole) {

			//J=Y-offset and I=X-offset
			path.append("G03 I0")
			.append(" J").append(format(-radius,4))
			.append(" Z").append(format(z,4))
			.append("\n");

			z -= machine.getCutDepthPerPass();
		}
		//do a final spiral to the exact depth
		path.append("G03 I0")
		.append(" J").append(format(-radius,4))
		.append(" Z").append(format( -depthOfAccessHole,4))
		.append("\n");

		//This is a through hole, no need to do a last cut at the bottom - but this would work
		//path.append("G03 I0").append(" J").append(format(-radius,4)).append("\n")

		return path.toString();
	}


	private static String drillHole(MachineService machine, double boreLocationX, double boreLocationY,
			double boreDiameter, double depthOfAccessHole) {
		StringBuilder path = new StringBuilder();
		double endMillRadius = 0.25 / 2.0;
		//the final depth
		double z = - depthOfAccessHole - machine.getCutDepthPerPass();

		//Adjust the center of the circle
		double centerX = boreLocationX - endMillRadius;
		double centerY = boreLocationY + endMillRadius;


		//go initial location on middle of track, away from the home
		path.append("G01 X").append(format(centerX,4))
		.append(" Y").append(format(centerY,4))
		.append(" F30\n");

		path.append("G01 Z").append(format(z, 4)).append("\n");
		//This is a through hole, no need to do a last cut at the bottom

		return path.toString();
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
	private static String tail(MachineService machine) {
		StringBuilder tail = new StringBuilder();
		
		//z-gap above material
		tail.append("G00 Z" + format(machine.getzGapAbove(), 4)).append(" ; (Move to safe clearance)\n");
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

}
