package com.t_slot_cnc.ui;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.model.Extrusion;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class LeftPanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = 8978598811708208648L;

	public LeftPanel(MainController controller, RightPanel rightPanel) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JRadioButton option1 = new JRadioButton("Option 1");
		JRadioButton option2 = new JRadioButton("Option 2");

		ButtonGroup group = new ButtonGroup();
		group.add(option1);
		group.add(option2);

		JButton okButton = new JButton("OK");

		option1.addActionListener(e -> controller.selectOption("OPTION_1"));
		option2.addActionListener(e -> controller.selectOption("OPTION_2"));

		okButton.addActionListener(e -> {
			String text = controller.onOkPressed();
			rightPanel.setText(text);
		});

		add(option1);
		add(option2);
		add(Box.createVerticalStrut(10));
		add(okButton);

		//Just for kicks show all the extrusions IDs
		List<Extrusion> exts = controller.getExtrusionSeries();
		for (Extrusion ext : exts) {
			System.out.println(ext.getId() + " d=" + ext.getAccessHole().getDiameter());
		}
	}

}
