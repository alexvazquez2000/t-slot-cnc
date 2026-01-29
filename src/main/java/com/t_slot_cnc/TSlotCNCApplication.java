package com.t_slot_cnc;

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
		var context = SpringApplication.run(TSlotCNCApplication.class, args);
		
		SwingUtilities.invokeLater(() -> {
			MainController controller = context.getBean(MainController.class);
			new MainFrame(controller).setVisible(true);
		});
	}

}
