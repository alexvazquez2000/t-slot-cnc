package com.t_slot_cnc.service;

import org.springframework.stereotype.Service;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class GCodeGeneratorService {
	public String generateText(String option) {
		return switch (option) {
		case "OPTION_1" -> "Generated text for Option 1.\nSpring-powered logic here.";
		case "OPTION_2" -> "Generated text for Option 2.\nDifferent business logic.";
		default -> "No option selected.";
		};
	}
}
