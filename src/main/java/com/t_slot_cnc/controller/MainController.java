package com.t_slot_cnc.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;
import com.t_slot_cnc.model.MachineSettings;
import com.t_slot_cnc.model.PartSelection;
import com.t_slot_cnc.model.SelectionModel;
import com.t_slot_cnc.service.BatchPartGenerationService;
import com.t_slot_cnc.service.ExtrusionsService;
import com.t_slot_cnc.service.FileNameService;
import com.t_slot_cnc.service.MachineSettingsService;
import com.t_slot_cnc.service.PartProgramService;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Component
public class MainController {
	private final SelectionModel model;
	private final ExtrusionsService extrusionService;
	private final MachineSettingsService machineSettingsService;
	private final PartProgramService partProgramService;
	private final BatchPartGenerationService batchPartGenerationService;


	public MainController(ExtrusionsService extrusionService, MachineSettingsService machineSettingsService, PartProgramService partProgramService,
			BatchPartGenerationService batchPartGenerationService) {
		this.extrusionService = extrusionService;
		this.machineSettingsService = machineSettingsService;
		this.partProgramService = partProgramService;
		this.batchPartGenerationService = batchPartGenerationService;
		this.model = new SelectionModel();
	}

	public void selectSeries(String option) {
		model.setSelectedSeries(option);
	}

	public void selectHoleType(HoleType holeType) {
		model.setSelectedHoleType(holeType);
	}

	public void selectColumns(int numColumns) {
		model.setNumColumns(numColumns);
	}

	public void selectRows(int numRows) {
		model.setNumRows(numRows);
	}

	public void selectHeightMultiplier(int heightMultiplier) {
		model.setHeightMultiplier(heightMultiplier);
	}

	public List<Extrusion> getExtrusionSeries() {
		return extrusionService.getExtrusions().getExtrusionSeries();
	}

	public PartSelection getPartSelection() {
		Extrusion extrusion = extrusionService.findExtrusionByName(model.getSelectedSeries());
		return new PartSelection(extrusion, model.getHoleType(), model.getNumColumns(), model.getNumRows(),
				model.getHeightMultiplier());
	}

	public void generateAllToolpaths() {
		try {
			batchPartGenerationService.generateAll(extrusionService.getExtrusions().getExtrusionSeries());
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate all toolpaths", e);
		}
	}

	public String getRecommendedFileName() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return "";
		int[] pattern = buildPattern(model.getNumColumns());
		if (model.getHoleType() == HoleType.COUNTERBORE) {
			return FileNameService.nameCounterbore(ext, ext.getCounterbore(), pattern.length);
		}
		return FileNameService.nameDrillHole(ext, pattern.length, model.getNumRows(), model.getHeightMultiplier());
	}

	public String generateGCode() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return "";
		int[] pattern = buildPattern(model.getNumColumns());
		if (model.getHoleType() == HoleType.COUNTERBORE) {
			return partProgramService.buildCounterboreText(ext, pattern);
		}
		return partProgramService.buildDrillHoleText(ext, pattern, model.getNumRows(), model.getHeightMultiplier());
	}

	public void saveGCode() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return;
		int[] pattern = buildPattern(model.getNumColumns());
		try {
			if (model.getHoleType() == HoleType.COUNTERBORE) {
				partProgramService.generateCounterbore(ext, pattern);
			} else {
				partProgramService.generateDrillHole(ext, pattern, model.getNumRows(), model.getHeightMultiplier());
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to save G-code", e);
		}
	}

	private int[] buildPattern(int numColumns) {
		int[] pattern = new int[numColumns];
		for (int i = 0; i < numColumns; i++) pattern[i] = i;
		return pattern;
	}

	public MachineSettings getMachineSettings() {
		return machineSettingsService.load();
	}

	public void saveMachineSettings(MachineSettings settings) {
		try {
			machineSettingsService.save(settings);
		} catch (IOException e) {
			throw new RuntimeException("Failed to save machine settings", e);
		}
	}
}
