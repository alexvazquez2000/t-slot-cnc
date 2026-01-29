package com.t_slot_cnc.controller;

import java.util.List;

import org.springframework.stereotype.Component;

import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.SelectionModel;
import com.t_slot_cnc.service.ExtrusionsService;
import com.t_slot_cnc.service.GCodeGeneratorService;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Component
public class MainController {
	private final SelectionModel model;
	private final GCodeGeneratorService gCodeService;
	private final ExtrusionsService extrusionService ;
	

	public MainController(ExtrusionsService extrusionService, GCodeGeneratorService gCodeService) {
		this.extrusionService = extrusionService;
		this.gCodeService = gCodeService;
		this.model = new SelectionModel();
	}

	public void selectOption(String option) {
		model.setSelectedOption(option);
	}

	public String onOkPressed() {
		return gCodeService.generateText(model.getSelectedOption());
	}

	public List<Extrusion> getExtrusionSeries() {
		return extrusionService.getExtrusions().getExtrusionSeries();
	}
}
