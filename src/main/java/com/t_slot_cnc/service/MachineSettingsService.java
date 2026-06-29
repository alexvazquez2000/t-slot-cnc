package com.t_slot_cnc.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.t_slot_cnc.model.MachineSettings;

/**
 * Loads and saves the machine cutting parameters used by {@link MachineService},
 * backed by specs/machine.properties (values are in inches).
 *
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class MachineSettingsService {

	private static final String CLASSPATH_RESOURCE = "/specs/machine.properties";
	private static final Path DEV_PATH = Path.of("src/main/resources/specs/machine.properties");

	private static Path propertiesPath() {
		if (System.getProperty("app.packaged") != null) {
			return Path.of(System.getProperty("user.home"), "T-Slot CNC", "specs", "machine.properties");
		}
		return DEV_PATH;
	}

	public MachineSettings load() {
		Path path = propertiesPath();

		if (System.getProperty("app.packaged") != null && !Files.exists(path)) {
			try {
				Files.createDirectories(path.getParent());
				try (InputStream in = MachineSettingsService.class.getResourceAsStream(CLASSPATH_RESOURCE);
					 OutputStream out = Files.newOutputStream(path)) {
					in.transferTo(out);
				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to seed machine.properties to " + path, e);
			}
		}

		Properties props = new Properties();
		try (InputStream is = Files.newInputStream(path)) {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load machine properties from " + path, e);
		}

		MachineSettings settings = new MachineSettings();
		settings.setEndMillDiameter(Double.parseDouble(props.getProperty("endMillDiameter")));
		settings.setFeedRate(Integer.parseInt(props.getProperty("feedRate")));
		settings.setDrillFeedRate(Integer.parseInt(props.getProperty("drillFeedRate")));
		settings.setSpindleSpeed(Integer.parseInt(props.getProperty("spindleSpeed")));
		settings.setCutDepthPerPass(Double.parseDouble(props.getProperty("cutDepthPerPass")));
		settings.setAccuracy(Double.parseDouble(props.getProperty("accuracy")));
		settings.setzGapAbove(Double.parseDouble(props.getProperty("zGapAbove")));
		return settings;
	}

	public void save(MachineSettings settings) throws IOException {
		Properties props = new Properties();
		props.setProperty("endMillDiameter", String.valueOf(settings.getEndMillDiameter()));
		props.setProperty("feedRate", String.valueOf(settings.getFeedRate()));
		props.setProperty("drillFeedRate", String.valueOf(settings.getDrillFeedRate()));
		props.setProperty("spindleSpeed", String.valueOf(settings.getSpindleSpeed()));
		props.setProperty("cutDepthPerPass", String.valueOf(settings.getCutDepthPerPass()));
		props.setProperty("accuracy", String.valueOf(settings.getAccuracy()));
		props.setProperty("zGapAbove", String.valueOf(settings.getzGapAbove()));

		Path path = propertiesPath();
		Files.createDirectories(path.getParent());
		try (OutputStream os = Files.newOutputStream(path)) {
			props.store(os, "Machine cutting parameters (inches)");
		}
	}
}
