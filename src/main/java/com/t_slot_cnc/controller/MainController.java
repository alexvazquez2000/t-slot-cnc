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

	public MainController(ExtrusionsService extrusionService, MachineSettingsService machineSettingsService,
			PartProgramService partProgramService, BatchPartGenerationService batchPartGenerationService) {
		this.extrusionService = extrusionService;
		this.machineSettingsService = machineSettingsService;
		this.partProgramService = partProgramService;
		this.batchPartGenerationService = batchPartGenerationService;
		this.model = new SelectionModel();
	}

	// ── Selection setters ────────────────────────────────────────────────────

	public void selectSeries(String option) {
		model.setSelectedSeries(option);
	}

	public void selectHoleType(HoleType holeType) {
		model.setSelectedHoleType(holeType);
	}

	public void selectColumns(int numColumns) {
		model.setNumColumns(numColumns); // also resets hole selection
	}

	public void selectRows(int numRows) {
		model.setNumRows(numRows); // also resets hole selection
	}

	public void selectHeightMultiplier(int heightMultiplier) {
		model.setHeightMultiplier(heightMultiplier);
	}

	public void selectMakeDivot(boolean makeDivot) {
		model.setMakeDivot(makeDivot);
	}

	/** Toggle a specific hole on or off. row and col are zero-indexed. */
	public void selectHole(int row, int col, boolean selected) {
		model.setHoleSelected(row, col, selected);
	}

	/** Reset all holes to selected (called when columns/rows change from the UI). */
	public void resetSelectedHoles() {
		model.resetSelectedHoles();
	}

	// ── Queries ──────────────────────────────────────────────────────────────

	public List<Extrusion> getExtrusionSeries() {
		return extrusionService.getExtrusions().getExtrusionSeries();
	}

	public PartSelection getPartSelection() {
		Extrusion extrusion = extrusionService.findExtrusionByName(model.getSelectedSeries());
		return new PartSelection(extrusion, model.getHoleType(), model.getNumColumns(), model.getNumRows(),
				model.getHeightMultiplier(), model.getSelectedHoles());
	}

	public String getRecommendedFileName() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return "";
		boolean[][] selected = model.getSelectedHoles();
		if (model.getHoleType() == HoleType.COUNTERBORE) {
			return FileNameService.nameCounterboreSelection(ext, selected);
		}
		return FileNameService.nameDrillHoleSelection(ext, selected, model.getHeightMultiplier());
	}

	public String generateGCode() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return "";
		boolean[][] selected = model.getSelectedHoles();
		if (model.getHoleType() == HoleType.COUNTERBORE) {
			return partProgramService.buildCounterboreText(ext, selected, model.isMakeDivot());
		}
		return partProgramService.buildDrillHoleText(ext, selected, model.getHeightMultiplier());
	}

	// ── Actions ──────────────────────────────────────────────────────────────

	public void saveGCode() {
		Extrusion ext = extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (ext == null) return;
		boolean[][] selected = model.getSelectedHoles();
		String gCode = model.getHoleType() == HoleType.COUNTERBORE
				? partProgramService.buildCounterboreText(ext, selected, model.isMakeDivot())
				: partProgramService.buildDrillHoleText(ext, selected, model.getHeightMultiplier());
		try {
			partProgramService.saveToFile(gCode, getRecommendedFileName());
		} catch (IOException e) {
			throw new RuntimeException("Failed to save G-code", e);
		}
	}

	public void generateAllToolpaths() {
		try {
			batchPartGenerationService.generateAll(extrusionService.getExtrusions().getExtrusionSeries());
		} catch (IOException e) {
			throw new RuntimeException("Failed to generate all toolpaths", e);
		}
	}

	// ── Machine settings ─────────────────────────────────────────────────────

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
