package com.t_slot_cnc.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;

/**
 * Generates the full G-code program for a single part (one counterbore, access-hole,
 * or drill-hole file) and writes it to disk, returning its spreadsheet description row.
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class PartProgramService {

	private final ToolpathService toolpathService;
	private final PartDescriptionService partDescriptionService;
	private final GCodeFileService gCodeFileService;

	public PartProgramService(ToolpathService toolpathService, PartDescriptionService partDescriptionService,
			GCodeFileService gCodeFileService) {
		this.toolpathService = toolpathService;
		this.partDescriptionService = partDescriptionService;
		this.gCodeFileService = gCodeFileService;
	}

	public String generateCounterbore(Extrusion ext, int[] pattern) throws IOException {
		String fileName = FileNameService.nameCounterbore(ext, ext.getCounterbore(), pattern.length);
		gCodeFileService.write(buildCounterboreText(ext, pattern), fileName);
		if (ext.getCounterbore() == null) return null;
		return partDescriptionService.describe(ext, fileName, new MachineService(ext.getUnits()));
	}

	public String buildCounterboreText(Extrusion ext, int[] columns) {
		StringBuilder response = new StringBuilder();
		Counterbore counterbore = ext.getCounterbore();
		if (counterbore == null) return "";

		MachineService machine = new MachineService(ext.getUnits());
		double boreLocationX = ext.getWidth() / 2.0;
		double boreLocationY = counterbore.getyOffset();
		double boreDiameter = counterbore.getDiameter();
		double depthOfBore = counterbore.getDepth();

		response.append(toolpathService.header(ext.getUnits(), machine));
		for (int column : columns) {
			if (ext.getId().startsWith("15-") || ext.getId().startsWith("40-")) {
				//rough cut
				response.append(toolpathService.counterbore(machine, boreLocationX + (column *ext.getWidth()) , boreLocationY,
						boreDiameter - (machine.getCutDepthPerPass() * 4),
						depthOfBore - (machine.getCutDepthPerPass() * 3) ));
				response.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(), 4)).append("; (15X or 40X series end of first rough pass x3)\n");
			}
			//rough cut
			response.append(toolpathService.counterbore(machine, boreLocationX + (column *ext.getWidth()) , boreLocationY,
					boreDiameter - (machine.getCutDepthPerPass() * 2),
					depthOfBore - (machine.getCutDepthPerPass() * 2) ));
			response.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(), 4)).append("; (end of rough pass x2)\n");

			//rough cut
			response.append(toolpathService.counterbore(machine, boreLocationX + (column *ext.getWidth()) , boreLocationY,
					boreDiameter - machine.getCutDepthPerPass(),
					depthOfBore - machine.getCutDepthPerPass() ));
			response.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(), 4)).append("; (end of second rough pass)\n");

			//final cut
			response.append(toolpathService.counterbore(machine, boreLocationX + (column *ext.getWidth()), boreLocationY,
					boreDiameter,
					depthOfBore ));
						
			response.append("G00 Z" + GCodeFormat.format(machine.getzGapAbove(), 4)).append("; (end of finish pass at 0.008)\n");
		}
		response.append(toolpathService.tail(machine));
		return response.toString();
	}

	/**
	 * @Deprecated ("2026-06-25 - not tested - only using drill holes")
	 */
	@Deprecated
	public String generateAccessHole(Extrusion ext, int[] columns, int rows, int multiplier) throws IOException {
		StringBuilder response = new StringBuilder();

		MachineService machine = new MachineService(ext.getUnits());

		AccessHole accessHole = ext.getAccessHole();
		double boreLocationX = ext.getWidth() / 2.0;
		double boreLocationY = accessHole.getyOffset();
		double accessHoleDiameter = accessHole.getDiameter();
		double topOfSlot = ext.getDepthToTopOfSlot();
		double depthOfAccessHole = computeHoleDepth(ext, machine, multiplier);

		String fileName = FileNameService.nameAccessHole(ext, accessHole, columns.length, rows, multiplier);
		String description = partDescriptionService.describe(ext, fileName, machine);

		response.append(toolpathService.header(ext.getUnits(), machine));
		for (int row=0; row < rows; row++) {
			for (int column : columns) {
				response.append("G00 X").append(GCodeFormat.format(boreLocationX +(column *ext.getWidth()), 4))
				.append(" Y").append(GCodeFormat.format(boreLocationY + (row * ext.getWidth()), 4))
				.append("\n");
				response.append(toolpathService.accessHole(machine, boreLocationX + (column *ext.getWidth()),
						boreLocationY + (row * ext.getWidth()), accessHoleDiameter, depthOfAccessHole, topOfSlot));
				response.append("G00 Z").append(GCodeFormat.format(machine.getzGapAbove(), 4)).append("\n");
			}
		}
		response.append(toolpathService.tail(machine));

		gCodeFileService.write(response.toString(), fileName);
		return description;
	}

	/**
	 * @param multiplier How tall is it?  1 or 2
	 */
	public String generateDrillHole(Extrusion ext, int[] pattern, int rows, int multiplier) throws IOException {
		String fileName = FileNameService.nameDrillHole(ext, pattern.length, rows, multiplier);
		gCodeFileService.write(buildDrillHoleText(ext, pattern, rows, multiplier), fileName);
		return partDescriptionService.describe(ext, fileName, new MachineService(ext.getUnits()));
	}

	/**
	 * @param multiplier How tall is it?  1 or 2
	 */
	public String buildDrillHoleText(Extrusion ext, int[] pattern, int rows, int multiplier) {
		StringBuilder response = new StringBuilder();
		MachineService machine = new MachineService(ext.getUnits());
		AccessHole accessHole = ext.getAccessHole();
		double boreLocationX = ext.getWidth() / 2.0;
		double boreLocationY = accessHole.getyOffset();
		double accessHoleDiameter = accessHole.getDiameter();
		double depthOfAccessHole = computeHoleDepth(ext, machine, multiplier);

		response.append(toolpathService.header(ext.getUnits(), machine));
		for (int row=0; row < rows; row++) {
			for (int p : pattern) {
				response.append("G00 X").append(GCodeFormat.format(boreLocationX +(p *ext.getWidth()), 4))
				.append(" Y").append(GCodeFormat.format(boreLocationY + (row * ext.getWidth()), 4))
				.append("\n");
				response.append(toolpathService.drillHole(machine, boreLocationX + (p *ext.getWidth()),
						boreLocationY + (row * ext.getWidth()), accessHoleDiameter, depthOfAccessHole));
				response.append("G00 Z").append(GCodeFormat.format(machine.getzGapAbove(), 4)).append("\n");
			}
		}
		response.append(toolpathService.tail(machine));
		return response.toString();
	}

	/**
	 * Depth of an axial access/drill hole, drilled in from the end of the extrusion.
	 *
	 * @param multiplier How tall is it?  1 or 2
	 */
	public static double computeHoleDepth(Extrusion ext, MachineService machine, int multiplier) {
		//maybe it should be in the specs, instead of calculating it here
		double percentOfWidth = 0.85;
		double depthOfHole;
		if (multiplier == 1) {
			depthOfHole = (ext.getWidth() * percentOfWidth) + machine.getCutDepthPerPass();
			if (depthOfHole > ext.getWidth()) {
				//make sure it is less than the width
				depthOfHole = ext.getWidth() * 0.85;
			}
		} else if (multiplier == 2) {
			depthOfHole = ext.getWidth() + (ext.getWidth() * percentOfWidth) + machine.getCutDepthPerPass();
			if (depthOfHole > multiplier * ext.getWidth()) {
				//make sure it is less than the width
				depthOfHole = multiplier * ext.getWidth() * 0.85;
			}
		} else {
			throw new RuntimeException("multiplier can only be 1 or 2");
		}
		return depthOfHole;
	}
}
