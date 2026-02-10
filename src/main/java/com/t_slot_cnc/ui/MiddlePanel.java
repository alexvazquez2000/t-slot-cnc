package com.t_slot_cnc.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MiddlePanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = -4293176338824464453L;

	private static final Logger logger = LoggerFactory.getLogger(MiddlePanel.class);

	private String defaultImage = "/10-series/EX-1010-ACCESS-HOLE-COUNTERBORE.png";
	private BufferedImage image;

	public MiddlePanel() {
		setLayout(new BorderLayout());
		setImage(defaultImage);
	}

	public void setImage(String imageName) {
		if (imageName == null) {
			logger.warn("Image file is null, skipping");
			return;
		}
		try {
			URL imageUrl = getClass().getResource(imageName);
			if (imageUrl != null) {
				image = ImageIO.read(imageUrl);
			}
		} catch (IOException e) {
			logger.warn("Error reading file {} exception={}", imageName, e);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g); // Call the super method
		if (image != null) {
			// Get the current width and height of the panel
			int panelWidth = this.getWidth();

			// Calculate the new height to maintain aspect ratio based on the panel's width
			int scaledHeight = (int) ((double) image.getHeight() * panelWidth / image.getWidth());

			// Draw the image scaled to the panel's width and the calculated height
			// Using Image.SCALE_SMOOTH for better quality
			Image scaledImage = image.getScaledInstance(panelWidth, scaledHeight, Image.SCALE_SMOOTH);

			// Draw the scaled image. It will fill the entire panel
			// The last argument 'this' is the ImageObserver
			g.drawImage(scaledImage, 0, 0, this);
		}
	}
}
