package com.t_slot_cnc.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.t_slot_cnc.model.MachineSettings;

/**
 * Lets the user view and edit the machine cutting parameters (specs/machine.properties).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineSettingsPanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = 1L;

	private final JTextField endMillDiameterField;
	private final JTextField feedRateField;
	private final JTextField drillFeedRateField;
	private final JTextField spindleSpeedField;
	private final JTextField cutDepthPerPassField;
	private final JTextField accuracyField;
	private final JTextField zGapAboveField;

	public MachineSettingsPanel(MachineSettings settings, Consumer<MachineSettings> onSave) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.anchor = GridBagConstraints.WEST;

		endMillDiameterField = new JTextField(String.valueOf(settings.getEndMillDiameter()), 10);
		feedRateField = new JTextField(String.valueOf(settings.getFeedRate()), 10);
		drillFeedRateField = new JTextField(String.valueOf(settings.getDrillFeedRate()), 10);
		spindleSpeedField = new JTextField(String.valueOf(settings.getSpindleSpeed()), 10);
		cutDepthPerPassField = new JTextField(String.valueOf(settings.getCutDepthPerPass()), 10);
		accuracyField = new JTextField(String.valueOf(settings.getAccuracy()), 10);
		zGapAboveField = new JTextField(String.valueOf(settings.getzGapAbove()), 10);

		int row = 0;
		row = addRow(c, row, "End Mill Diameter (in):", endMillDiameterField);
		row = addRow(c, row, "Feed Rate (in/min):", feedRateField);
		row = addRow(c, row, "Drill Feed Rate (in/min):", drillFeedRateField);
		row = addRow(c, row, "Spindle Speed:", spindleSpeedField);
		row = addRow(c, row, "Cut Depth Per Pass (in):", cutDepthPerPassField);
		row = addRow(c, row, "Accuracy (in):", accuracyField);
		row = addRow(c, row, "Z Gap Above (in):", zGapAboveField);

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> save(onSave));

		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 2;
		add(saveButton, c);
	}

	private void save(Consumer<MachineSettings> onSave) {
		try {
			MachineSettings updated = new MachineSettings();
			updated.setEndMillDiameter(Double.parseDouble(endMillDiameterField.getText().trim()));
			updated.setFeedRate(Integer.parseInt(feedRateField.getText().trim()));
			updated.setDrillFeedRate(Integer.parseInt(drillFeedRateField.getText().trim()));
			updated.setSpindleSpeed(Integer.parseInt(spindleSpeedField.getText().trim()));
			updated.setCutDepthPerPass(Double.parseDouble(cutDepthPerPassField.getText().trim()));
			updated.setAccuracy(Double.parseDouble(accuracyField.getText().trim()));
			updated.setzGapAbove(Double.parseDouble(zGapAboveField.getText().trim()));

			onSave.accept(updated);
			JOptionPane.showMessageDialog(this, "Machine settings saved.");
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Please enter valid numbers for all fields.",
					"Invalid input", JOptionPane.ERROR_MESSAGE);
		}
	}

	private int addRow(GridBagConstraints c, int row, String label, JTextField field) {
		c.gridx = 0;
		c.gridy = row;
		c.gridwidth = 1;
		add(new JLabel(label), c);

		c.gridx = 1;
		add(field, c);

		return row + 1;
	}

}
