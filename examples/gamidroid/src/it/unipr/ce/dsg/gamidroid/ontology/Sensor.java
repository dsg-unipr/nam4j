package it.unipr.ce.dsg.gamidroid.ontology;

import it.unipr.ce.dsg.nam4j.impl.service.Parameter;

public class Sensor extends Parameter {

	/** The type of the sensor (e.g. temperature, light, noise, ...) */
	private String type;

	public Sensor() {
		this.setName("Sensor");
	}

	public Sensor(String value) {
		this.setName("Sensor");
		this.setValue(value);
	}

	public Sensor(String value, String type) {
		this.setName("Sensor");
		this.setValue(value);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
