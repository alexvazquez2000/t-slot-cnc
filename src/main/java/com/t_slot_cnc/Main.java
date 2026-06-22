package com.t_slot_cnc;

import java.io.IOException;

import com.t_slot_cnc.service.BatchPartGenerationService;
import com.t_slot_cnc.service.ExcelService;
import com.t_slot_cnc.service.ExtrusionsService;
import com.t_slot_cnc.service.GCodeFileService;
import com.t_slot_cnc.service.PartDescriptionService;
import com.t_slot_cnc.service.PartProgramService;
import com.t_slot_cnc.service.ToolpathService;

/**
 * Kept as a standalone entry point (outside the Spring context the GUI uses) so the
 * generated output files can be regenerated and diffed while the GUI panels are
 * still being wired up.
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class Main {

	public static void main(String[] args) throws IOException {
		ExtrusionsService extrusionsService = new ExtrusionsService();
		extrusionsService.loadSpecs();

		ToolpathService toolpathService = new ToolpathService();
		GCodeFileService gCodeFileService = new GCodeFileService();
		PartProgramService partProgramService = new PartProgramService(toolpathService, new PartDescriptionService(), gCodeFileService);
		BatchPartGenerationService batchPartGenerationService = new BatchPartGenerationService(
				partProgramService, toolpathService, gCodeFileService, new ExcelService());

		batchPartGenerationService.generateAll(extrusionsService.getExtrusions().getExtrusionSeries());
	}

}
