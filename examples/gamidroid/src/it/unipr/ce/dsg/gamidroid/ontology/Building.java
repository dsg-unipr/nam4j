package it.unipr.ce.dsg.gamidroid.ontology;

import it.unipr.ce.dsg.nam4j.impl.service.Parameter;

public class Building extends Parameter {
	

	public Building() {
		this.setName("Building");
	}
	
	public Building( String value ) {
		this.setName("Building");
		this.setValue(value);
	}

}
