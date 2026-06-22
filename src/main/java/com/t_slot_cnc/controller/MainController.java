package com.t_slot_cnc.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;

import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;
import com.t_slot_cnc.model.MachineSettings;
import com.t_slot_cnc.model.SelectionModel;
import com.t_slot_cnc.service.ExtrusionsService;
import com.t_slot_cnc.service.GCodeGeneratorService;
import com.t_slot_cnc.service.MachineSettingsService;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Component
public class MainController {
	private final SelectionModel model;
	private final GCodeGeneratorService gCodeService;
	private final ExtrusionsService extrusionService ;
	private final MachineSettingsService machineSettingsService;


	public MainController(ExtrusionsService extrusionService, GCodeGeneratorService gCodeService,
			MachineSettingsService machineSettingsService) {
		this.extrusionService = extrusionService;
		this.gCodeService = gCodeService;
		this.machineSettingsService = machineSettingsService;
		this.model = new SelectionModel();
	}

	public void selectSeries(String option) {
		model.setSelectedSeries(option);
	}

	public void selectHoleType(HoleType holeType) {
		model.setSelectedHoleType(holeType);
	}

	public String onOkPressed() {
		return gCodeService.generateText(model.getSelectedSeries() + model.getHoleType().getName());
	}

	public List<Extrusion> getExtrusionSeries() {
		return extrusionService.getExtrusions().getExtrusionSeries();
	}

	public String getImageName() {
		Extrusion extrusion= extrusionService.findExtrusionByName(model.getSelectedSeries());
		if (model.getHoleType()== HoleType.COUNTERBORE) {
			if (extrusion.getCounterbore() == null) {
				return null;
			} else {
				return extrusion.getCounterbore().getImage();
			}
		}
		return extrusion.getAccessHole().getImage();
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
