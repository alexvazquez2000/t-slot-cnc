package com.t_slot_cnc.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.t_slot_cnc.model.Extrusion;

/**
 * Generates every counterbore, access-hole, and drill-hole part file for every
 * extrusion series, the parts spreadsheet, and the return-to-vice program.
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class BatchPartGenerationService {

	private final PartProgramService partProgramService;
	private final ToolpathService toolpathService;
	private final GCodeFileService gCodeFileService;
	private final ExcelService excelService;

	public BatchPartGenerationService(PartProgramService partProgramService, ToolpathService toolpathService,
			GCodeFileService gCodeFileService, ExcelService excelService) {
		this.partProgramService = partProgramService;
		this.toolpathService = toolpathService;
		this.gCodeFileService = gCodeFileService;
		this.excelService = excelService;
	}

	public void generateAll(List<Extrusion> extrusionSeries) throws IOException {
		List<String> partDescriptions = new ArrayList<>();

		for (Extrusion ext : extrusionSeries) {

			if (ext.getCounterbore() != null) {
				partDescriptions.add(partProgramService.generateCounterbore(ext, new int[] {0}));
				partDescriptions.add(partProgramService.generateCounterbore(ext, new int[] {0,1}));
				partDescriptions.add(partProgramService.generateCounterbore(ext, new int[] {0,1,2}));
				partDescriptions.add(partProgramService.generateCounterbore(ext, new int[] {0,1,2,3}));
			}

			for (int rows = 1; rows <=2; rows++) {
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0}, rows, 1));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0}, rows, 2));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1}, rows, 1));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1}, rows, 2));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1,2}, rows, 1));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1,2}, rows, 2));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1,2,3}, rows, 1));
				partDescriptions.add(partProgramService.generateDrillHole(ext, new int[] {0,1,2,3}, rows, 2));

				//Access holes
				//partDescriptions.add(partProgramService.generateAccessHole(ext, new int[] {0}, rows));
				//partDescriptions.add(partProgramService.generateAccessHole(ext, new int[] {0,1}, rows));
				//partDescriptions.add(partProgramService.generateAccessHole(ext, new int[] {0,1,2}, rows));
				//partDescriptions.add(partProgramService.generateAccessHole(ext, new int[] {0,1,2,3}, rows));
			}
		}

		excelService.save(partDescriptions, FileNameService.outputRoot() + "parts.xlsx");

		//return to origin - params in millimeters
		String gCode = toolpathService.generateReturnToVice(124.245, 68.406);
		gCodeFileService.write(gCode, FileNameService.outputRoot() + "returnToVice.nc");
	}
}
