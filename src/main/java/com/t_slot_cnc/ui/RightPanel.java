package com.t_slot_cnc.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class RightPanel extends BorderPane {

	private final TextField fileNameField;
	private final TextArea textArea;

	public RightPanel(Runnable onSave) {
		setPadding(new Insets(4));

		fileNameField = new TextField();
		fileNameField.setEditable(false);
		fileNameField.setFont(Font.font("Monospaced", 11));

		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			try {
				onSave.run();
				Alert alert = new Alert(Alert.AlertType.INFORMATION, "G-code saved to:\n" + fileNameField.getText());
				alert.showAndWait();
			} catch (Exception ex) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "Save failed: " + ex.getMessage());
				alert.showAndWait();
			}
		});

		HBox topPanel = new HBox(4, fileNameField, saveButton);
		HBox.setHgrow(fileNameField, Priority.ALWAYS);
		topPanel.setPadding(new Insets(0, 0, 4, 0));

		textArea = new TextArea();
		textArea.setEditable(false);
		textArea.setFont(Font.font("Monospaced", 11));
		textArea.setWrapText(false);

		ScrollPane scrollPane = new ScrollPane(textArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		setTop(topPanel);
		setCenter(scrollPane);
	}

	public void setFileName(String fileName) {
		fileNameField.setText(fileName);
	}

	public void setText(String text) {
		textArea.setText(text);
		textArea.positionCaret(0);
	}

}
