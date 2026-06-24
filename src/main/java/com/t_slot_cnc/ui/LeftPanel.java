package com.t_slot_cnc.ui;

import java.util.List;
import java.util.function.IntConsumer;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class LeftPanel extends VBox {

	private HBox rowsBox;
	private HBox heightMultiplierBox;
	private ToggleGroup rowsGroup;

	public LeftPanel(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		setSpacing(4);
		setPadding(new Insets(6));
		setPrefWidth(170);

		// Extrusion series
		ToggleGroup seriesGroup = new ToggleGroup();
		List<Extrusion> exts = controller.getExtrusionSeries();
		for (int i = 0; i < exts.size(); i++) {
			Extrusion ext = exts.get(i);
			RadioButton rb = new RadioButton(ext.getId());
			rb.setToggleGroup(seriesGroup);
			rb.setOnAction(e -> {
				controller.selectSeries(ext.getId());
				refresh(controller, middlePanel, rightPanel);
			});
			if (i == 0) {
				rb.setSelected(true);
				controller.selectSeries(ext.getId());
			}
			getChildren().add(rb);
		}

		getChildren().add(new Separator());

		// Hole type
		ToggleGroup holeGroup = new ToggleGroup();
		RadioButton accessHoleRb = new RadioButton(HoleType.ACCESS_HOLE.getName());
		accessHoleRb.setToggleGroup(holeGroup);
		accessHoleRb.setSelected(true);
		controller.selectHoleType(HoleType.ACCESS_HOLE);

		RadioButton counterboreRb = new RadioButton(HoleType.COUNTERBORE.getName());
		counterboreRb.setToggleGroup(holeGroup);

		getChildren().addAll(accessHoleRb, counterboreRb);
		getChildren().add(new Separator());

		// Columns / Rows / Height Multiplier
		ToggleGroup columnsGroup = new ToggleGroup();
		HBox columnsBox = createIntChoiceBox("Columns:", new int[] {1,2,3,4}, 1,
				value -> { controller.selectColumns(value); refresh(controller, middlePanel, rightPanel); },
				columnsGroup);

		rowsGroup = new ToggleGroup();
		rowsBox = createIntChoiceBox("Rows:", new int[] {1,2}, 1,
				value -> { controller.selectRows(value); refresh(controller, middlePanel, rightPanel); },
				rowsGroup);

		ToggleGroup multiplierGroup = new ToggleGroup();
		heightMultiplierBox = createIntChoiceBox("Height x:", new int[] {1,2}, 1,
				value -> { controller.selectHeightMultiplier(value); refresh(controller, middlePanel, rightPanel); },
				multiplierGroup);

		getChildren().addAll(columnsBox, rowsBox, heightMultiplierBox);
		getChildren().add(new Separator());

		// Wire hole type radio buttons after the panels they control are created
		accessHoleRb.setOnAction(e -> {
			controller.selectHoleType(HoleType.ACCESS_HOLE);
			rowsBox.setDisable(false);
			heightMultiplierBox.setDisable(false);
			refresh(controller, middlePanel, rightPanel);
		});
		counterboreRb.setOnAction(e -> {
			controller.selectHoleType(HoleType.COUNTERBORE);
			selectInGroup(rowsGroup, 1);
			controller.selectRows(1);
			rowsBox.setDisable(true);
			heightMultiplierBox.setDisable(true);
			refresh(controller, middlePanel, rightPanel);
		});

		// Generate all toolpaths button
		Button generateAllButton = new Button("Generate all toolpaths");
		generateAllButton.setMaxWidth(Double.MAX_VALUE);
		generateAllButton.setOnAction(e -> {
			try {
				controller.generateAllToolpaths();
				new Alert(Alert.AlertType.INFORMATION, "All toolpaths generated successfully.").show();
			} catch (Exception ex) {
				new Alert(Alert.AlertType.ERROR, "Generation failed: " + ex.getMessage()).show();
			}
		});
		getChildren().add(generateAllButton);

		refresh(controller, middlePanel, rightPanel);
	}

	private void refresh(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		middlePanel.updateDiagram(controller.getPartSelection());
		rightPanel.setFileName(controller.getRecommendedFileName());
		rightPanel.setText(controller.generateGCode());
	}

	private HBox createIntChoiceBox(String label, int[] options, int defaultValue,
			IntConsumer onSelect, ToggleGroup group) {
		HBox box = new HBox(4);
		box.getChildren().add(new Label(label));
		for (int option : options) {
			RadioButton rb = new RadioButton(String.valueOf(option));
			rb.setToggleGroup(group);
			if (option == defaultValue) rb.setSelected(true);
			rb.setOnAction(e -> onSelect.accept(option));
			box.getChildren().add(rb);
		}
		onSelect.accept(defaultValue);
		return box;
	}

	private void selectInGroup(ToggleGroup group, int value) {
		group.getToggles().stream()
				.filter(t -> ((RadioButton) t).getText().equals(String.valueOf(value)))
				.findFirst()
				.ifPresent(group::selectToggle);
	}

}
