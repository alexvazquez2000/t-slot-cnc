package com.t_slot_cnc.service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
public class FileNameService {

	private static final String FILE_EXTENSION = ".nc";

	private FileNameService() {
		//Don't instantiate - only static members
	}

	/**
	 * Returns the root output directory. When running as a packaged EXE
	 * (detected via -Dapp.packaged=true in jpackage --java-options), files go
	 * to the user's Documents folder so the install directory stays read-only.
	 * In development the path stays "output/" relative to the working directory.
	 */
	public static String outputRoot() {
		if (System.getProperty("app.packaged") != null) {
			return System.getProperty("user.home") + "/Documents/T-Slot CNC/output/";
		}
		return "output/";
	}

	public static String nameCounterbore(Extrusion ext, Counterbore counterbore, int numColumns) {
		return outputRoot() + ext.getId() + "/counterbore/" + ext.getId().substring(0,2) + "X_cb" + columnPattern(numColumns) + FILE_EXTENSION;
	}

	public static String nameDrillHole(Extrusion ext, int numColumns, int rows, int multipier) {
		String series = ext.getId().substring(0,2);
		if (multipier == 2) {
			series = (Integer.valueOf(series) * 2 ) + "";
		}
		return outputRoot() + ext.getId() + "/drill_hole/" + series + "_dh" + columnPattern(numColumns) + rows + FILE_EXTENSION;
	}

	public static String nameAccessHole(Extrusion ext, AccessHole accessHole, int numColumns, int rows, int multipier) {
		return outputRoot() + ext.getId() + "/access_hole/"  + ext.getId().substring(0,2) + "X_ah" + columnPattern(numColumns) + rows + "_" + multipier + FILE_EXTENSION;
	}

	/**
	 * Names a drill-hole file based on which specific holes are selected (GUI use).
	 * Columns that are active in at least one selected row appear in the name;
	 * the row count reflects how many rows have at least one selected hole.
	 */
	public static String nameDrillHoleSelection(Extrusion ext, boolean[][] selected, int multiplier) {
		String series = ext.getId().substring(0, 2);
		if (multiplier == 2) series = (Integer.valueOf(series) * 2) + "";
		return outputRoot() + ext.getId() + "/drill_hole/SP_" + series + "_dh" + columnLetters(selected)
				+ activeRows(selected) + FILE_EXTENSION;
	}

	/**
	 * Names a counterbore file based on which specific holes are selected (GUI use).
	 */
	public static String nameCounterboreSelection(Extrusion ext, boolean[][] selected) {
		return outputRoot() + ext.getId() + "/counterbore/SP_" + ext.getId().substring(0, 2) + "X_cb"
				+ columnLetters(selected) + FILE_EXTENSION;
	}

	private static String columnPattern(int pattern) {
		switch (pattern) {
		case 1: return "_A_";
		case 2: return "_A_B_";
		case 3: return "_A_B_C_";
		case 4: return "_A_B_C_D_";
		default:
			throw new RuntimeException("Unsupported number of columns = " + pattern);
		}
	}

	private static final char[] COL_LETTERS = {'A', 'B', 'C', 'D'};

	/** Builds a "_A_C_" style string from columns active in at least one row. */
	private static String columnLetters(boolean[][] selected) {
		StringBuilder sb = new StringBuilder("_");
		for (int col = 0; col < 4; col++) {
			for (int row = 0; row < 2; row++) {
				if (selected[row][col]) {
					sb.append(COL_LETTERS[col]).append('_');
					break;
				}
			}
		}
		return sb.toString();
	}

	/** Count of rows that have at least one selected hole. */
	private static int activeRows(boolean[][] selected) {
		int count = 0;
		for (boolean[] row : selected)
			for (boolean cell : row)
				if (cell) { count++; break; }
		return count;
	}

}
