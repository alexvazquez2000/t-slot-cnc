package com.t_slot_cnc.ui;

import java.awt.BorderLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MiddlePanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = -4293176338824464453L;

	public MiddlePanel() {
		setLayout(new BorderLayout());

		URL imageUrl = getClass().getResource("/10-series/EX-1010-ACCESS-HOLE-COUNTERBORE.png");
		if (imageUrl != null) {
			JLabel imageLabel = new JLabel(new ImageIcon(imageUrl));
			imageLabel.setHorizontalAlignment(JLabel.CENTER);
			add(imageLabel, BorderLayout.CENTER);
		} else {
			add(new JLabel("Image not found", JLabel.CENTER), BorderLayout.CENTER);
		}
	}

}
