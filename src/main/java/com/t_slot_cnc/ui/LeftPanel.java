package com.t_slot_cnc.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.IntConsumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class LeftPanel extends JPanel implements ActionListener {

	/** Serialize */
	private static final long serialVersionUID = 8978598811708208648L;

	private static final Logger logger = LoggerFactory.getLogger(LeftPanel.class); // Get the SLF4J logger instance

	private MainController controller;
	private MiddlePanel middlePanel;
	private JPanel heightMultiplierPanel;

	public LeftPanel(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		this.controller = controller;
		this.middlePanel = middlePanel;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(80, getPreferredSize().height));
		
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


		JRadioButton accessHoleRadio = new JRadioButton(HoleType.ACCESS_HOLE.getName());
		accessHoleRadio.setSelected(true);
		accessHoleRadio.setName(HoleType.ACCESS_HOLE.getName());
		controller.selectHoleType(HoleType.ACCESS_HOLE);

		accessHoleRadio.addActionListener(this);
		JRadioButton counterboreRadio = new JRadioButton(HoleType.COUNTERBORE.getName());
		counterboreRadio.addActionListener(this);
		counterboreRadio.setName(HoleType.COUNTERBORE.getName());

		ButtonGroup holeGroup = new ButtonGroup();
		holeGroup.add(accessHoleRadio);
		holeGroup.add(counterboreRadio);

		//
		add(Box.createVerticalStrut(10));
		add(accessHoleRadio);
		add(counterboreRadio);

		add(Box.createVerticalStrut(10));
		add(createIntChoicePanel("Columns:", new int[] {1,2,3,4}, 1,
				value -> { controller.selectColumns(value); refreshDiagram(); }));
		add(createIntChoicePanel("Rows:", new int[] {1,2}, 1,
				value -> { controller.selectRows(value); refreshDiagram(); }));

		heightMultiplierPanel = createIntChoicePanel("Height Multiplier:", new int[] {1,2}, 1,
				value -> { controller.selectHeightMultiplier(value); refreshDiagram(); });
		add(heightMultiplierPanel);
		//Height multiplier only applies to Access Hole - Counterbore is unaffected by stacked extrusions
		setEnabledRecursively(heightMultiplierPanel, true);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			String text = controller.onOkPressed();
			rightPanel.setText(text);
		});

		add(Box.createVerticalStrut(10));
		add(okButton);

		refreshDiagram();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JRadioButton radioButton) {
			if (radioButton.getName().equals(HoleType.ACCESS_HOLE.getName())) {
				controller.selectHoleType(HoleType.ACCESS_HOLE);
				setEnabledRecursively(heightMultiplierPanel, true);
			} else if (radioButton.getName().equals(HoleType.COUNTERBORE.getName())) {
				controller.selectHoleType(HoleType.COUNTERBORE);
				setEnabledRecursively(heightMultiplierPanel, false);
			} else {
				controller.selectSeries(radioButton.getName());
			}
			refreshDiagram();
		} else {
			logger.warn("Ignoring action {} d={}" , e.getSource(), e.getActionCommand());
		}
	}

	private void refreshDiagram() {
		middlePanel.updateDiagram(controller.getPartSelection());
		middlePanel.repaint();
	}

	/**
	 * Builds a labeled row of radio buttons for picking one of a small set of integer values.
	 */
	private JPanel createIntChoicePanel(String label, int[] options, int defaultValue, IntConsumer onSelect) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(label));

		ButtonGroup choiceGroup = new ButtonGroup();
		for (int option : options) {
			JRadioButton radio = new JRadioButton(String.valueOf(option));
			radio.setSelected(option == defaultValue);
			radio.addActionListener(e -> onSelect.accept(option));
			choiceGroup.add(radio);
			panel.add(radio);
		}

		onSelect.accept(defaultValue);
		return panel;
	}

	private void setEnabledRecursively(Component component, boolean enabled) {
		component.setEnabled(enabled);
		if (component instanceof Container container) {
			for (Component child : container.getComponents()) {
				setEnabledRecursively(child, enabled);
			}
		}
	}

}
