package com.t_slot_cnc.ui;

import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.model.Extrusion;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class LeftPanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = 8978598811708208648L;

	private static final Logger logger = LoggerFactory.getLogger(LeftPanel.class); // Get the SLF4J logger instance

	public LeftPanel(MainController controller, RightPanel rightPanel) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		ButtonGroup group = new ButtonGroup();
		List<Extrusion> exts = controller.getExtrusionSeries();
		//Create radio buttons for each extrusion
		JRadioButton[] radioExt = new JRadioButton[exts.size()];
		int i = 0;
		for (Extrusion ext : exts) {
			radioExt[i] = new JRadioButton(ext.getId());
			group.add(radioExt[i]);
			radioExt[i].addActionListener(e -> controller.selectOption(ext.getId()));
			add(radioExt[i]);
			
			logger.warn("id={} d={}" , ext.getId(), ext.getAccessHole().getDiameter());
			if (i==0) {
				//select the first one - we could also save the last choice made and select it
				radioExt[i].setSelected(true);
				controller.selectOption(ext.getId());
			}

			i++;
		}
		
		ButtonGroup holeGroup = new ButtonGroup();
		JRadioButton accessHoleRadio = new JRadioButton("Access hole");
		JRadioButton counterboreRadio = new JRadioButton("Counterbore");
		JRadioButton comboAHCounterRadio = new JRadioButton("Combo Access/Counter");
		
		accessHoleRadio.setSelected(true);
		
		holeGroup.add(accessHoleRadio);
		holeGroup.add(counterboreRadio);
		holeGroup.add(comboAHCounterRadio);
		
		//
		add(Box.createVerticalStrut(10));
		add(accessHoleRadio);
		add(counterboreRadio);
		add(comboAHCounterRadio);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			String text = controller.onOkPressed();
			rightPanel.setText(text);
		});

		add(Box.createVerticalStrut(10));
		add(okButton);
	}

}
