package it.unipr.ce.dsg.examples.ontology;

import it.unipr.ce.dsg.nam4j.impl.service.Parameter;

public class Room extends Parameter {

	public Room() {
		this.setName("Room");
	}

	public Room(String value) {
		this.setName("Room");
		this.setValue(value);
	}

}
