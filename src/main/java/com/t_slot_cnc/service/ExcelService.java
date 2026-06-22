package com.t_slot_cnc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

	private static final String[] HEADERS = {"ID", "File", "Units", "Type",
			"Y Offset", "X Center", "Size", "Width",
			"Y Offset", "X Center", "Size", "Width"
			};

	public void save(List<String> rows, String fileName) throws IOException {
		Path file = Paths.get(fileName);
		File parentDir = file.toFile().getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Parts");

			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < HEADERS.length; i++) {
				headerRow.createCell(i).setCellValue(HEADERS[i]);
			}

			int rowNum = 1;
			for (String line : rows) {
				String[] cols = line.split("\t");
				Row row = sheet.createRow(rowNum++);
				for (int i = 0; i < cols.length; i++) {
					row.createCell(i).setCellValue(cols[i]);
				}
			}

			for (int i = 0; i < HEADERS.length; i++) {
				sheet.autoSizeColumn(i);
			}

			try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
				workbook.write(fos);
			}
		}
		System.out.println("Successfully wrote the spreadsheet to " + fileName);
	}
}
