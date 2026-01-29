package com.t_slot_cnc.service;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.t_slot_cnc.model.Extrusions;

import java.io.InputStream;

/**
 * @author Alex Vazquez <vazqueza2000@gmail.com>
 */
@Service
public class ExtrusionsService {
	private Extrusions extrusions;

	/**
	 * Loaded once at startup and accessible anywhere via Spring
	 */
	@PostConstruct
	public void loadSpecs() {
		try {
			
			JAXBContext context = JAXBContext.newInstance(Extrusions.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			ClassPathResource resource = new ClassPathResource("specs/seriesConfig.xml");

			try (InputStream is = resource.getInputStream()) {
				extrusions = (Extrusions) unmarshaller.unmarshal(is);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load Extrusions specs XML", e);
		}
	}

	public Extrusions getExtrusions() {
		return extrusions;
	}

}
