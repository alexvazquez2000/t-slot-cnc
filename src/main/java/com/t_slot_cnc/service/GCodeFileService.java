package com.t_slot_cnc.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class GCodeFileService {

	public void write(String gCode, String fileName) throws IOException {
		Path file = Paths.get(fileName);

		//Make sure the directory exists
		File parentDir = file.toFile().getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}

		// This will create a new file or overwrite an existing one
		Files.writeString(file, gCode);
		System.out.println("Successfully wrote the string to the file " + file.toString());
	}
}
