package com.t_slot_cnc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class LeftPanel extends JPanel implements ActionListener {

	/** Serialize */
	private static final long serialVersionUID = 8978598811708208648L;

	private static final Logger logger = LoggerFactory.getLogger(LeftPanel.class); // Get the SLF4J logger instance

	private MainController controller;

	private static final String ACCESS_HOLE = "Access Hole";
	private static final String COUNTERBORE = "Counterbore";

	public LeftPanel(MainController controller, RightPanel rightPanel) {
		this.controller = controller;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		ButtonGroup group = new ButtonGroup();
		List<Extrusion> exts = controller.getExtrusionSeries();
		//Create radio buttons for each extrusion
		JRadioButton[] radioExt = new JRadioButton[exts.size()];
		int i = 0;
		for (Extrusion ext : exts) {
			radioExt[i] = new JRadioButton(ext.getId());
			radioExt[i].setName(ext.getId());
			group.add(radioExt[i]);
			radioExt[i].addActionListener(this);
			add(radioExt[i]);
			
			logger.warn("id={} d={}" , ext.getId(), ext.getAccessHole().getDiameter());
			if (i==0) {
				//select the first one - we could also save the last choice made and select it
				radioExt[i].setSelected(true);
				controller.selectSeries(ext.getId());
			}

			i++;
		}
		
		
		JRadioButton accessHoleRadio = new JRadioButton(ACCESS_HOLE);
		accessHoleRadio.setSelected(true);
		accessHoleRadio.setName(ACCESS_HOLE);
		
		accessHoleRadio.addActionListener(this);
		JRadioButton counterboreRadio = new JRadioButton(COUNTERBORE);
		counterboreRadio.addActionListener(this);
		counterboreRadio.setName(COUNTERBORE);
		
		ButtonGroup holeGroup = new ButtonGroup();
		holeGroup.add(accessHoleRadio);
		holeGroup.add(counterboreRadio);
		
		//
		add(Box.createVerticalStrut(10));
		add(accessHoleRadio);
		add(counterboreRadio);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			String text = controller.onOkPressed();
			rightPanel.setText(text);
		});

		add(Box.createVerticalStrut(10));
		add(okButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButton radioButton) {
			switch (radioButton.getName()) {
				case ACCESS_HOLE, COUNTERBORE -> controller.selectHoleType(radioButton.getName());
				default -> controller.selectSeries(radioButton.getName());
			}
		} else {
			logger.warn("Ignoring action {} d={}" , e.getSource(), e.getActionCommand());
		}
	}

}
