package com.t_slot_cnc.ui;

import java.util.function.Consumer;

import com.t_slot_cnc.model.MachineSettings;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Lets the user view and edit the machine cutting parameters (specs/machine.properties).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MachineSettingsPanel extends GridPane {

	private final TextField endMillDiameterField;
	private final TextField feedRateField;
	private final TextField drillFeedRateField;
	private final TextField spindleSpeedField;
	private final TextField cutDepthPerPassField;
	private final TextField accuracyField;
	private final TextField zGapAboveField;

	public MachineSettingsPanel(MachineSettings settings, Consumer<MachineSettings> onSave) {
		setHgap(8);
		setVgap(6);
		setPadding(new Insets(10));

		ColumnConstraints labelCol = new ColumnConstraints();
		labelCol.setHgrow(Priority.NEVER);
		ColumnConstraints fieldCol = new ColumnConstraints();
		fieldCol.setHgrow(Priority.ALWAYS);
		fieldCol.setFillWidth(true);
		getColumnConstraints().addAll(labelCol, fieldCol);

		endMillDiameterField = new TextField(String.valueOf(settings.getEndMillDiameter()));
		feedRateField = new TextField(String.valueOf(settings.getFeedRate()));
		drillFeedRateField = new TextField(String.valueOf(settings.getDrillFeedRate()));
		spindleSpeedField = new TextField(String.valueOf(settings.getSpindleSpeed()));
		cutDepthPerPassField = new TextField(String.valueOf(settings.getCutDepthPerPass()));
		accuracyField = new TextField(String.valueOf(settings.getAccuracy()));
		zGapAboveField = new TextField(String.valueOf(settings.getzGapAbove()));

		int row = 0;
		row = addRow(row, "End Mill Diameter (in):", endMillDiameterField);
		row = addRow(row, "Feed Rate (in/min):", feedRateField);
		row = addRow(row, "Drill Feed Rate (in/min):", drillFeedRateField);
		row = addRow(row, "Spindle Speed:", spindleSpeedField);
		row = addRow(row, "Cut Depth Per Pass (in):", cutDepthPerPassField);
		row = addRow(row, "Accuracy (in):", accuracyField);
		row = addRow(row, "Z Gap Above (in):", zGapAboveField);

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> save(onSave));
		add(saveButton, 0, row, 2, 1);
	}

	private int addRow(int row, String label, TextField field) {
		add(new Label(label), 0, row);
		add(field, 1, row);
		return row + 1;
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
			new Alert(Alert.AlertType.INFORMATION, "Machine settings saved.").showAndWait();
		} catch (NumberFormatException ex) {
			new Alert(Alert.AlertType.ERROR, "Please enter valid numbers for all fields.").showAndWait();
		}
	}

}
