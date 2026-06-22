package com.t_slot_cnc.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;
import com.t_slot_cnc.model.PartSelection;

/**
 * Draws a dimensioned diagram of a single extrusion piece: a length view showing the
 * hole grid (columns spaced across the width, rows spaced down the length), and an end
 * view showing the piece's cross-section thickness (the height multiplier).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MiddlePanel extends JPanel {

	/** Serialize */
	private static final long serialVersionUID = -4293176338824464453L;

	private static final int MARGIN = 40;
	private static final int TITLE_HEIGHT = 30;
	private static final int SECTION_LABEL_HEIGHT = 18;
	private static final int SECTION_GAP = 20;
	private static final int DIMENSION_OFFSET = 18;
	private static final double MIN_SCALE = 8;
	private static final double MAX_SCALE = 150;
	private static final double END_VIEW_HEIGHT_FRACTION = 0.3;

	private PartSelection selection;

	public MiddlePanel() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
	}

	public void updateDiagram(PartSelection selection) {
		this.selection = selection;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (selection == null || selection.getExtrusion() == null) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		try {
			draw(g2);
		} finally {
			g2.dispose();
		}
	}

	private void draw(Graphics2D g2) {
		Extrusion extrusion = selection.getExtrusion();
		HoleType holeType = selection.getHoleType();
		int columns = Math.max(1, selection.getNumColumns());
		int rows = Math.max(1, selection.getNumRows());
		int heightMultiplier = Math.max(1, selection.getHeightMultiplier());

		String units = extrusion.getUnits().equals("inches")?"\"":"mm";
		double diameter;
		double yOffset;
		String calloutText;
		if (holeType == HoleType.COUNTERBORE) {
			Counterbore counterbore = extrusion.getCounterbore();
			if (counterbore == null) {
				drawCenteredMessage(g2, "No counterbore defined for " + extrusion.getId());
				return;
			}
			diameter = counterbore.getDiameter();
			yOffset = counterbore.getyOffset();
			calloutText = "Ø" + diameter + units +" x " + counterbore.getDepth() + units + " deep";
		} else {
			AccessHole accessHole = extrusion.getAccessHole();
			diameter = accessHole.getDiameter();
			yOffset = accessHole.getyOffset();
			calloutText = "Ø" + diameter + units + " TYP.";
		}

		double unitWidth = extrusion.getWidth();

		drawTitle(g2, extrusion.getId() + " - " + holeType.getName());

		int contentTop = TITLE_HEIGHT + MARGIN / 2;
		int contentHeight = getHeight() - contentTop - MARGIN;
		int endViewHeight = (int) (contentHeight * END_VIEW_HEIGHT_FRACTION);
		int lengthViewHeight = contentHeight - endViewHeight - SECTION_GAP - 2 * SECTION_LABEL_HEIGHT;

		int lengthViewTop = contentTop + SECTION_LABEL_HEIGHT;
		drawSectionLabel(g2, "Length View", contentTop);
		drawLengthView(g2, columns, rows, unitWidth, yOffset, diameter, calloutText, lengthViewTop, lengthViewHeight);

		int endViewLabelTop = lengthViewTop + lengthViewHeight + SECTION_GAP;
		int endViewTop = endViewLabelTop + SECTION_LABEL_HEIGHT;
		drawSectionLabel(g2, "End View (cross-section thickness)", endViewLabelTop);
		drawEndView(g2, columns, unitWidth, heightMultiplier, endViewTop, endViewHeight);
	}

	private void drawLengthView(Graphics2D g2, int columns, int rows, double unitWidth, double yOffset,
			double diameter, String calloutText, int top, int availableHeight) {
		double pieceWidthInches = columns * unitWidth;
		double pieceLengthInches = yOffset * 2 + (rows - 1) * unitWidth;

		int availableWidth = getWidth() - 2 * MARGIN;
		double scale = computeScale(availableWidth, availableHeight, pieceWidthInches, pieceLengthInches);

		int pieceWidthPx = (int) Math.round(pieceWidthInches * scale);
		int pieceHeightPx = (int) Math.round(pieceLengthInches * scale);
		int barX = (getWidth() - pieceWidthPx) / 2;

		g2.setStroke(new BasicStroke(1.5f));
		g2.setColor(Color.DARK_GRAY);
		g2.draw(new Rectangle2D.Double(barX, top, pieceWidthPx, pieceHeightPx));
		drawBreak(g2, barX, top + pieceHeightPx, pieceWidthPx);

		double holeDiameterPx = Math.max(4, diameter * scale);
		boolean calloutDrawn = false;
		for (int r = 0; r < rows; r++) {
			int holeY = top + (int) Math.round((yOffset + r * unitWidth) * scale);

			for (int c = 0; c < columns; c++) {
				int holeX = barX + (int) Math.round((c + 0.5) * unitWidth * scale);

				g2.setColor(Color.BLACK);
				g2.draw(new Ellipse2D.Double(holeX - holeDiameterPx / 2, holeY - holeDiameterPx / 2,
						holeDiameterPx, holeDiameterPx));

				if (r == 0 && c == 1) {
					drawHorizontalDimension(g2, barX, barX + (int) Math.round(unitWidth * scale),
							top - DIMENSION_OFFSET, unitWidth);
				}

				if (!calloutDrawn) {
					drawCallout(g2, holeX + (int) (holeDiameterPx / 2), holeY, calloutText);
					calloutDrawn = true;
				}
			}

			double fromInches = (r == 0) ? yOffset : unitWidth;
			int fromY = (r == 0) ? top : top + (int) Math.round((yOffset + (r - 1) * unitWidth) * scale);
			drawVerticalDimension(g2, barX - DIMENSION_OFFSET, fromY, holeY, fromInches);
		}
	}

	private void drawEndView(Graphics2D g2, int columns, double unitWidth, int heightMultiplier, int top,
			int availableHeight) {
		double pieceWidthInches = columns * unitWidth;
		double thicknessInches = unitWidth * heightMultiplier;

		int availableWidth = getWidth() - 2 * MARGIN;
		double scale = computeScale(availableWidth, availableHeight, pieceWidthInches, thicknessInches);

		int pieceWidthPx = (int) Math.round(pieceWidthInches * scale);
		int thicknessPx = (int) Math.round(thicknessInches * scale);
		int barX = (getWidth() - pieceWidthPx) / 2;

		g2.setStroke(new BasicStroke(1.5f));
		g2.setColor(Color.DARK_GRAY);
		g2.draw(new Rectangle2D.Double(barX, top, pieceWidthPx, thicknessPx));

		//Mark the boundary between each column's module
		for (int c = 1; c < columns; c++) {
			int x = barX + (int) Math.round(c * unitWidth * scale);
			g2.setColor(Color.LIGHT_GRAY);
			g2.draw(new Line2D.Double(x, top, x, top + thicknessPx));
		}

		drawVerticalDimension(g2, barX - DIMENSION_OFFSET, top, top + thicknessPx, thicknessInches);
		drawHorizontalDimension(g2, barX, barX + pieceWidthPx, top + thicknessPx + DIMENSION_OFFSET, pieceWidthInches);
	}

	private double computeScale(int availableWidth, int availableHeight, double widthInches, double lengthInches) {
		double scaleByWidth = availableWidth / widthInches;
		double scaleByHeight = availableHeight / lengthInches;

		double scale = Math.min(scaleByWidth, scaleByHeight);
		return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
	}

	private void drawTitle(Graphics2D g2, String title) {
		g2.setColor(Color.BLACK);
		g2.setFont(g2.getFont().deriveFont(Font.BOLD, 16f));
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, TITLE_HEIGHT);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
	}

	private void drawSectionLabel(Graphics2D g2, String label, int top) {
		g2.setColor(Color.GRAY);
		g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 11f));
		g2.drawString(label, MARGIN, top + SECTION_LABEL_HEIGHT - 4);
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 12f));
	}

	private void drawBreak(Graphics2D g2, int barX, int y, int barWidthPx) {
		//Jagged line indicating the extrusion continues beyond what's drawn
		int teeth = 6;
		int step = Math.max(1, barWidthPx / teeth);
		int zigzag = 4;
		g2.setColor(Color.DARK_GRAY);
		int x = barX;
		int prevY = y;
		for (int i = 0; i < teeth; i++) {
			int nextX = barX + (i + 1) * step;
			int nextY = (i % 2 == 0) ? y - zigzag : y + zigzag;
			g2.draw(new Line2D.Double(x, prevY, nextX, nextY));
			x = nextX;
			prevY = nextY;
		}
	}

	private void drawVerticalDimension(Graphics2D g2, int x, int fromY, int toY, double valueInches) {
		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(x, fromY, x, toY));
		g2.draw(new Line2D.Double(x - 4, fromY, x + 4, fromY));
		g2.draw(new Line2D.Double(x - 4, toY, x + 4, toY));

		String label = String.valueOf(valueInches);
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(Color.BLACK);
		g2.drawString(label, x - fm.stringWidth(label) - 4, (fromY + toY) / 2);
	}

	private void drawHorizontalDimension(Graphics2D g2, int fromX, int toX, int y, double valueInches) {
		g2.setColor(Color.GRAY);
		g2.draw(new Line2D.Double(fromX, y, toX, y));
		g2.draw(new Line2D.Double(fromX, y - 4, fromX, y + 4));
		g2.draw(new Line2D.Double(toX, y - 4, toX, y + 4));

		String label = String.valueOf(valueInches);
		FontMetrics fm = g2.getFontMetrics();
		g2.setColor(Color.BLACK);
		g2.drawString(label, (fromX + toX - fm.stringWidth(label)) / 2, y - 6);
	}

	private void drawCallout(Graphics2D g2, int x, int y, String text) {
		g2.setColor(Color.BLACK);
		int yOffset = 10;
		g2.draw(new Line2D.Double(x, y, x + 20, y + yOffset));
		g2.drawString(text, x + 24, y + 4 + yOffset);
	}

	private void drawCenteredMessage(Graphics2D g2, String message) {
		g2.setColor(Color.BLACK);
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() / 2);
	}

}
