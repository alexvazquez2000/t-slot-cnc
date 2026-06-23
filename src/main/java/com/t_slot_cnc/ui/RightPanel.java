package com.t_slot_cnc.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class RightPanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = 2529204967880222630L;

	private final JTextField fileNameField;
	private final JTextArea textArea;

	public RightPanel(Runnable onSave) {
		setLayout(new BorderLayout(0, 4));
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		JPanel topPanel = new JPanel(new BorderLayout(4, 0));
		fileNameField = new JTextField();
		fileNameField.setEditable(false);
		fileNameField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(e -> {
			try {
				onSave.run();
				JOptionPane.showMessageDialog(this, "G-code saved to:\n" + fileNameField.getText());
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		topPanel.add(fileNameField, BorderLayout.CENTER);
		topPanel.add(saveButton, BorderLayout.EAST);
		add(topPanel, BorderLayout.NORTH);

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
		textArea.setLineWrap(false);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	public void setFileName(String fileName) {
		fileNameField.setText(fileName);
	}

	public void setText(String text) {
		textArea.setText(text);
		textArea.setCaretPosition(0);
	}

}
