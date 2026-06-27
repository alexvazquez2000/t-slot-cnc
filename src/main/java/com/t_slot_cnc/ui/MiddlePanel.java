package com.t_slot_cnc.ui;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;
import com.t_slot_cnc.model.HoleType;
import com.t_slot_cnc.model.PartSelection;
import com.t_slot_cnc.service.GCodeFormat;
import com.t_slot_cnc.service.MachineService;
import com.t_slot_cnc.service.PartProgramService;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Draws a dimensioned diagram of a single extrusion piece: a length view showing the
 * hole grid (columns spaced across the width, rows spaced down the length), and an end
 * view showing the piece's cross-section thickness (the height multiplier).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class MiddlePanel extends Region {

	private static final int MARGIN = 60;
	private static final int TITLE_HEIGHT = 30;
	private static final int SECTION_LABEL_HEIGHT = 18;
	private static final int SECTION_GAP = 20;
	private static final int DIMENSION_OFFSET = 18;
	private static final double MIN_SCALE = 1;
	private static final double MAX_SCALE = 150;
	private static final double END_VIEW_HEIGHT_FRACTION = 0.3;

	private final Canvas canvas = new Canvas();
	private PartSelection selection;

	public MiddlePanel() {
		setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		getChildren().add(canvas);
	}

	@Override
	protected void layoutChildren() {
		canvas.setWidth(getWidth());
		canvas.setHeight(getHeight());
		redraw();
	}

	public void updateDiagram(PartSelection selection) {
		this.selection = selection;
		redraw();
	}

	private void redraw() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (selection == null || selection.getExtrusion() == null) return;
		draw(gc);
	}

	private void draw(GraphicsContext gc) {
		Extrusion extrusion = selection.getExtrusion();
		HoleType holeType = selection.getHoleType();
		int columns = Math.max(1, selection.getNumColumns());
		int rows = Math.max(1, selection.getNumRows());
		int heightMultiplier = Math.max(1, selection.getHeightMultiplier());
		double depthOfHole = 0.0;

		String units = extrusion.getUnits().equals("inches") ? "\"" : "mm";
		double diameter, yOffset;
		String calloutText;

		if (holeType == HoleType.COUNTERBORE) {
			Counterbore counterbore = extrusion.getCounterbore();
			if (counterbore == null) {
				drawCenteredMessage(gc, "No counterbore defined for " + extrusion.getId());
				return;
			}
			diameter = counterbore.getDiameter();
			yOffset = counterbore.getyOffset();
			calloutText = "Ø" + diameter + units + " x " + counterbore.getDepth() + units + " deep";
			depthOfHole = counterbore.getDepth();
		} else {
			AccessHole accessHole = extrusion.getAccessHole();
			diameter = accessHole.getDiameter();
			yOffset = accessHole.getyOffset();
			MachineService machine = new MachineService(extrusion.getUnits());
			depthOfHole = PartProgramService.computeHoleDepth(extrusion, machine, heightMultiplier);
			calloutText = "Ø" + diameter + units + " TYP. x " + GCodeFormat.format(depthOfHole, 4) + units + " deep";
		}

		double unitWidth = extrusion.getWidth();

		drawTitle(gc, extrusion.getId() + " - " + holeType.getName());

		int contentTop = TITLE_HEIGHT + MARGIN / 2;
		int contentHeight = (int) canvas.getHeight() - contentTop - MARGIN;
		int endViewHeight = (int) (contentHeight * END_VIEW_HEIGHT_FRACTION);
		int lengthViewHeight = contentHeight - endViewHeight - SECTION_GAP - 2 * SECTION_LABEL_HEIGHT;

		int lengthViewTop = contentTop + SECTION_LABEL_HEIGHT;
		drawSectionLabel(gc, "Length View", contentTop);
		drawLengthView(gc, selection, columns, rows, unitWidth, yOffset, diameter, calloutText, lengthViewTop, lengthViewHeight);

		int endViewLabelTop = lengthViewTop + lengthViewHeight + SECTION_GAP;
		int endViewTop = endViewLabelTop + SECTION_LABEL_HEIGHT;
		drawSectionLabel(gc, "End View (cross-section thickness)", endViewLabelTop);
		drawEndView(gc, columns, unitWidth, heightMultiplier, endViewTop, endViewHeight, diameter, depthOfHole);
	}

	private void drawLengthView(GraphicsContext gc, PartSelection sel, int columns, int rows, double unitWidth,
			double yOffset, double diameter, String calloutText, int top, int availableHeight) {
		double pieceWidthInches = columns * unitWidth;
		double pieceLengthInches = yOffset * 3 + (rows - 1) * unitWidth;

		int availableWidth = (int) canvas.getWidth() - 2 * MARGIN;
		double scale = computeScale(availableWidth, availableHeight, pieceWidthInches, pieceLengthInches);

		int pieceWidthPx = (int) Math.round(pieceWidthInches * scale);
		int pieceHeightPx = (int) Math.round(pieceLengthInches * scale);
		int barX = ((int) canvas.getWidth() - pieceWidthPx) / 2;
		int bottom = top + pieceHeightPx;

		gc.setLineWidth(1.5);
		gc.setStroke(Color.DARKGRAY);
		gc.strokeRect(barX, top, pieceWidthPx, pieceHeightPx);
		drawBreak(gc, barX, top, pieceWidthPx);

		// End mill starting position: left edge at barX, top edge at bottom of rectangle
		MachineService machine = new MachineService(sel.getExtrusion().getUnits());
		double endMillRadiusPx = (machine.getEndMillDiameter() / 2.0) * scale;
		double endMillCenterX = barX + endMillRadiusPx;
		double endMillCenterY = bottom + endMillRadiusPx;
		gc.setStroke(Color.DARKORANGE);
		gc.setLineWidth(1.5);
		gc.strokeOval(endMillCenterX - endMillRadiusPx, endMillCenterY - endMillRadiusPx,
				endMillRadiusPx * 2, endMillRadiusPx * 2);
		// crosshair at end mill center
		gc.strokeLine(endMillCenterX - endMillRadiusPx * 0.4, endMillCenterY,
				endMillCenterX + endMillRadiusPx * 0.4, endMillCenterY);
		gc.strokeLine(endMillCenterX, endMillCenterY - endMillRadiusPx * 0.4,
				endMillCenterX, endMillCenterY + endMillRadiusPx * 0.4);

		double holeDiameterPx = Math.max(4, diameter * scale);
		boolean calloutDrawn = false;

		for (int r = 0; r < rows; r++) {
			int holeY = bottom - (int) Math.round((yOffset + r * unitWidth) * scale);

			for (int c = 0; c < columns; c++) {
				int holeX = barX + (int) Math.round((c + 0.5) * unitWidth * scale);
				boolean selected = sel.isSelected(r, c);

				if (selected) {
					gc.setStroke(Color.BLACK);
					gc.setLineWidth(1.5);
					gc.setLineDashes(0);
				} else {
					gc.setStroke(Color.LIGHTGRAY);
					gc.setLineWidth(1.0);
					gc.setLineDashes(4, 3);
				}
				gc.strokeOval(holeX - holeDiameterPx / 2, holeY - holeDiameterPx / 2, holeDiameterPx, holeDiameterPx);
				gc.setLineDashes(0);

				if (r == 0 && c == 1) {
					drawHorizontalDimension(gc, barX, barX + (int) Math.round(unitWidth * scale),
							bottom + DIMENSION_OFFSET, unitWidth);
				}

				if (!calloutDrawn && selected) {
					drawCallout(gc, holeX + (int) (holeDiameterPx / 2), holeY, holeDiameterPx, calloutText);
					calloutDrawn = true;
				}
			}

			double fromInches = (r == 0) ? yOffset : unitWidth;
			int fromY = (r == 0) ? bottom : bottom - (int) Math.round((yOffset + (r - 1) * unitWidth) * scale);
			drawVerticalDimension(gc, barX - DIMENSION_OFFSET, holeY, fromY, fromInches);
		}
	}

	private void drawEndView(GraphicsContext gc, int columns, double unitWidth, int heightMultiplier, int top,
			int availableHeight, double diameter, double depthOfHole) {
		double pieceWidthInches = columns * unitWidth;
		double thicknessInches = unitWidth * heightMultiplier;

		int availableWidth = (int) canvas.getWidth() - 2 * MARGIN;
		double scale = computeScale(availableWidth, availableHeight, pieceWidthInches, thicknessInches);

		int pieceWidthPx = (int) Math.round(pieceWidthInches * scale);
		int thicknessPx = (int) Math.round(thicknessInches * scale);
		int barX = ((int) canvas.getWidth() - pieceWidthPx) / 2;

		gc.setLineWidth(1.5);
		gc.setStroke(Color.DARKGRAY);
		gc.strokeRect(barX, top, pieceWidthPx, thicknessPx);

		// Module-boundary dividers
		gc.setLineWidth(1.0);
		gc.setStroke(Color.LIGHTGRAY);
		for (int c = 1; c < columns; c++) {
			int x = barX + (int) Math.round(c * unitWidth * scale);
			gc.strokeLine(x, top, x, top + thicknessPx);
		}

		// X marks per column module
		gc.setStroke(Color.LIGHTGRAY);
		gc.setLineWidth(2.5);
		for (int c = 0; c < columns; c++) {
			int x1 = barX + (int) Math.round(c * unitWidth * scale);
			int x2 = barX + (int) Math.round((c + 1) * unitWidth * scale);

			gc.strokeLine(x1, top, x2, top + unitWidth * scale);
			gc.strokeLine(x2, top, x1, top + unitWidth * scale);

			if (heightMultiplier == 2) {
				double tempTop = top + unitWidth * scale;
				gc.strokeLine(x1, tempTop, x2, tempTop + unitWidth * scale);
				gc.strokeLine(x2, tempTop, x1, tempTop + unitWidth * scale);
			}

			// Hole depth rectangle
			int holeWidthPx = (int) Math.round(diameter * scale);
			int holeX = (int) ((x1 + x2) / 2.0 - holeWidthPx / 2.0);
			gc.setFill(Color.GRAY);
			gc.fillRect(holeX, top, holeWidthPx, Math.round(depthOfHole * scale));
		}

		gc.setLineWidth(1.5);
		drawVerticalDimension(gc, barX - DIMENSION_OFFSET, top, top + thicknessPx, thicknessInches);
		drawHorizontalDimension(gc, barX, barX + pieceWidthPx, top + thicknessPx + DIMENSION_OFFSET, pieceWidthInches);
	}

	private double computeScale(int availableWidth, int availableHeight, double widthInches, double lengthInches) {
		if (widthInches <= 0 || lengthInches <= 0) return MIN_SCALE;
		double scale = Math.min(availableWidth / widthInches, availableHeight / lengthInches);
		return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
	}

	private void drawTitle(GraphicsContext gc, String title) {
		gc.setFont(Font.font("System", FontWeight.BOLD, 16));
		gc.setFill(Color.BLACK);
		gc.fillText(title, (canvas.getWidth() - textWidth(gc, title)) / 2, TITLE_HEIGHT);
		gc.setFont(Font.font("System", 12));
	}

	private void drawSectionLabel(GraphicsContext gc, String label, int top) {
		gc.setFont(Font.font("System", FontPosture.ITALIC, 11));
		gc.setFill(Color.GRAY);
		gc.fillText(label, MARGIN, top + SECTION_LABEL_HEIGHT - 4);
		gc.setFont(Font.font("System", 12));
	}

	private void drawBreak(GraphicsContext gc, int barX, int y, int barWidthPx) {
		int teeth = 6;
		int step = Math.max(1, barWidthPx / teeth);
		int zigzag = 4;
		gc.setStroke(Color.DARKGRAY);
		gc.setLineWidth(1.5);
		int x = barX, prevY = y;
		for (int i = 0; i < teeth; i++) {
			int nextX = barX + (i + 1) * step;
			int nextY = (i % 2 == 0) ? y - zigzag : y + zigzag;
			gc.strokeLine(x, prevY, nextX, nextY);
			x = nextX;
			prevY = nextY;
		}
	}

	private void drawVerticalDimension(GraphicsContext gc, int x, int fromY, int toY, double valueInches) {
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1.0);
		gc.strokeLine(x, fromY, x, toY);
		gc.strokeLine(x - 4, fromY, x + 4, fromY);
		gc.strokeLine(x - 4, toY, x + 4, toY);
		String label = String.valueOf(valueInches);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font("System", 12));
		gc.fillText(label, x - textWidth(gc, label) - 4, (fromY + toY) / 2.0);
	}

	private void drawHorizontalDimension(GraphicsContext gc, int fromX, int toX, int y, double valueInches) {
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1.0);
		gc.strokeLine(fromX, y, toX, y);
		gc.strokeLine(fromX, y - 4, fromX, y + 4);
		gc.strokeLine(toX, y - 4, toX, y + 4);
		String label = String.valueOf(valueInches);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font("System", 12));
		gc.fillText(label, (fromX + toX - textWidth(gc, label)) / 2, y - 6);
	}

	private void drawCallout(GraphicsContext gc, int x, int y, double holeDiameter, String text) {
		int yOff = (int) (holeDiameter * 1.2);
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(1.0);
		gc.strokeLine(x, y, x + 20, y + yOff);
		gc.setFill(Color.BLACK);
		gc.setFont(Font.font("System", 12));
		gc.fillText(text, x + 24, y + 4 + yOff);
	}

	private void drawCenteredMessage(GraphicsContext gc, String message) {
		gc.setFont(Font.font("System", 12));
		gc.setFill(Color.BLACK);
		gc.fillText(message, (canvas.getWidth() - textWidth(gc, message)) / 2, canvas.getHeight() / 2);
	}

	private double textWidth(GraphicsContext gc, String text) {
		Text helper = new Text(text);
		helper.setFont(gc.getFont());
		return helper.getLayoutBounds().getWidth();
	}

}
