package com.t_slot_cnc.ui;

import java.util.List;
import java.util.function.IntConsumer;

import com.t_slot_cnc.controller.MainController;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class LeftPanel extends VBox {

	private static final char[] COL_LABELS = {'A', 'B', 'C', 'D'};

	private HBox rowsBox;
	private HBox heightMultiplierBox;
	private ToggleGroup rowsGroup;
	private GridPane holeGrid;

	private int currentNumColumns = 1;
	private int currentNumRows = 1;

	public LeftPanel(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		setSpacing(4);
		setPadding(new Insets(6));
		setPrefWidth(190);

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

		// Initialize hole grid before creating the choice boxes that reference it
		holeGrid = new GridPane();
		holeGrid.setHgap(4);
		holeGrid.setVgap(4);
		holeGrid.setPadding(new Insets(4, 0, 0, 0));

		// Columns / Rows / Height Multiplier
		ToggleGroup columnsGroup = new ToggleGroup();
		HBox columnsBox = createIntChoiceBox("Columns:", new int[] {1,2,3,4}, 1, value -> {
			currentNumColumns = value;
			controller.selectColumns(value); // also resets selectedHoles
			rebuildHoleGrid(controller, middlePanel, rightPanel);
			refresh(controller, middlePanel, rightPanel);
		}, columnsGroup);

		rowsGroup = new ToggleGroup();
		rowsBox = createIntChoiceBox("Rows:", new int[] {1,2}, 1, value -> {
			currentNumRows = value;
			controller.selectRows(value); // also resets selectedHoles
			rebuildHoleGrid(controller, middlePanel, rightPanel);
			refresh(controller, middlePanel, rightPanel);
		}, rowsGroup);

		ToggleGroup multiplierGroup = new ToggleGroup();
		heightMultiplierBox = createIntChoiceBox("Height x:", new int[] {1,2}, 1,
				value -> { controller.selectHeightMultiplier(value); refresh(controller, middlePanel, rightPanel); },
				multiplierGroup);

		getChildren().addAll(columnsBox, rowsBox, heightMultiplierBox);
		getChildren().add(holeGrid); // already populated by the lambdas fired above

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
			currentNumRows = 1;
			controller.selectRows(1);
			rebuildHoleGrid(controller, middlePanel, rightPanel);
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

	// ── Hole grid ──────────────────────────────────────────────────────────

	private void rebuildHoleGrid(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		holeGrid.getChildren().clear();
		buildHoleGrid(controller, middlePanel, rightPanel, currentNumRows, currentNumColumns);
	}

	private void buildHoleGrid(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel,
			int numRows, int numCols) {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numCols; col++) {
				String label = String.valueOf(COL_LABELS[col]) + (row + 1);
				ToggleButton tb = new ToggleButton(label);
				tb.setSelected(true);
				tb.setPrefWidth(44);

				final int r = row, c = col;
				tb.setOnAction(e -> {
					controller.selectHole(r, c, tb.isSelected());
					refresh(controller, middlePanel, rightPanel);
				});

				GridPane.setConstraints(tb, col, row);
				holeGrid.getChildren().add(tb);
			}
		}
	}

	// ── Refresh ────────────────────────────────────────────────────────────

	private void refresh(MainController controller, MiddlePanel middlePanel, RightPanel rightPanel) {
		middlePanel.updateDiagram(controller.getPartSelection());
		rightPanel.setFileName(controller.getRecommendedFileName());
		rightPanel.setText(controller.generateGCode());
	}

	// ── Helpers ────────────────────────────────────────────────────────────

	private HBox createIntChoiceBox(String label, int[] options, int defaultValue,
			IntConsumer onSelect, ToggleGroup group) {
		HBox box = new HBox(4);
		box.setAlignment(Pos.CENTER_LEFT);
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
