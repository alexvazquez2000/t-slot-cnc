package com.t_slot_cnc.service;

import org.springframework.stereotype.Service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;

/**
 * Builds the tab-separated row describing a generated part for the parts spreadsheet.
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class PartDescriptionService {

	public String describe(Extrusion ext, String fileName, MachineService machine) {
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
					part.append("\t")
					.append(GCodeFormat.format(counterbore.getyOffset() /25.4, 3)).append("\"\t")
					.append(GCodeFormat.format((ext.getWidth()/2.0) /25.4, 3)).append("\"\t")
					.append(GCodeFormat.format((counterbore.getDiameter() /25.4), 3)
							+ "\" x " + GCodeFormat.format(counterbore.getDepth()/25.4, 3) + "\" deep").append("\t")
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
			.append(ext.getWidth());
		}

		return part.toString();
	}
}
