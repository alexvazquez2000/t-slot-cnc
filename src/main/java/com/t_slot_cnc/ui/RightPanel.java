package com.t_slot_cnc.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class RightPanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = 2529204967880222630L;

	private final JTextArea textArea;

	public RightPanel() {
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	public void setText(String text) {
		textArea.setText(text);
	}

}
