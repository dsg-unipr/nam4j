package it.unipr.ce.dsg.gamidroid.ontology;

import it.unipr.ce.dsg.nam4j.impl.service.Parameter;

public class Floor extends Parameter {

	public Floor() {
		this.setName("Floor");
	}

	public Floor( String value ) {
		this.setName("Floor");
		this.setValue( value );
	}
	
}
