package com.t_slot_cnc;

import java.awt.GraphicsEnvironment;

import javax.swing.SwingUtilities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.ui.MainFrame;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@SpringBootApplication
public class TSlotCNCApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(TSlotCNCApplication.class);
		// This is needed to not be headless
		app.setHeadless(false);
		var context = app.run(args);
		if (GraphicsEnvironment.isHeadless()) {
			throw new IllegalStateException("No graphics, running in headless mode!");
		} else {
			SwingUtilities.invokeLater(() -> {
				MainController controller = context.getBean(MainController.class);
				new MainFrame(controller).setVisible(true);
			});
		}
	}

}
