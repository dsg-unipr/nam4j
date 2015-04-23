package it.unipr.ce.dsg.gamidroid.ontology;

import java.util.ArrayList;
import java.util.List;

public class RoomStruct {
	
	private String name;
	private List<Sensor> sensors;
	
	public RoomStruct( ) {
		sensors = new ArrayList<Sensor>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

}
