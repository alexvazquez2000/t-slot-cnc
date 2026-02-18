package com.t_slot_cnc.service;

import com.t_slot_cnc.model.AccessHole;
import com.t_slot_cnc.model.Counterbore;
import com.t_slot_cnc.model.Extrusion;

public class FileNameService {

	private static String fileExtension = ".txt";

	public static String nameCounterbore(Extrusion ext, Counterbore counterbore, int numColumns) {
		//fileName +=  "X_cb_A_" + counterbore.getPartNumber() + fileExtension
		//fileName += "X_cb_A_B_C_D_" + counterbore.getPartNumber() + fileExtension
		
		return "output/" + ext.getId() + "/" + ext.getId().substring(0,2) + "X_cb" + columnPattern(numColumns) + counterbore.getPartNumber() + fileExtension;
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

	public static String nameDrillHole(Extrusion ext, int numColumns, int rows, int multipier) {
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "_dh_A_" + rows + ".txt"
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "_2020_dh_A_" + rows + ".txt"
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "_dh_A_B_C_D" + rows + ".txt"
		
		if (multipier == 2) {
			return "output/" + ext.getId() + "/"  + ext.getId().substring(0,2) + "_2020_dh" + columnPattern(numColumns) + rows + fileExtension;
		}
		return "output/" + ext.getId() + "/"  + ext.getId().substring(0,2) + "_dh" + columnPattern(numColumns) + rows + fileExtension;
	}

	public static String nameAccessHole(Extrusion ext, AccessHole accessHole, int numColumns, int rows) {
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "X_ah_A_" + rows + "_" + accessHole.getPartNumber()+ ".txt"
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "X_ah_A_B_" + rows + "_" + accessHole.getPartNumber()+ ".txt"
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "X_ah_A_B_C_" + rows + "_" + accessHole.getPartNumber()+ ".txt"
		//fileName = outputDir + "/" + ext.getId().substring(0,2) + "X_ah_A_B_C_D_" + rows + "_" + accessHole.getPartNumber() + ".txt"
		return "output/" + ext.getId() + "/"  + ext.getId().substring(0,2) + "X_ah" + columnPattern(numColumns) + rows + "_" + accessHole.getPartNumber() + fileExtension;
	}

}
