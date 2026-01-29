package com.t_slot_cnc.controller;

import org.springframework.stereotype.Component;

import com.t_slot_cnc.model.SelectionModel;
import com.t_slot_cnc.service.GCodeGeneratorService;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Component
public class MainController {
	private final SelectionModel model;
	private final GCodeGeneratorService service;

	public MainController(GCodeGeneratorService service) {
		this.service = service;
		this.model = new SelectionModel();
	}

	public void selectOption(String option) {
		model.setSelectedOption(option);
	}

	public String onOkPressed() {
		return service.generateText(model.getSelectedOption());
	}

}
