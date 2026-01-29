package com.t_slot_cnc.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "extrusions")
public class Extrusions {
	private List<Extrusion> extrusionSeries;

	@XmlElement(name = "extrusion")
	public List<Extrusion> getExtrusionSeries() {
		return extrusionSeries;
	}

	public void setExtrusionSeries(List<Extrusion> extrusionSeries) {
		this.extrusionSeries = extrusionSeries;
	}

}
