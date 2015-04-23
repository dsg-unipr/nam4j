package it.unipr.ce.dsg.gamidroid.ontology;


import java.util.ArrayList;
import java.util.List;


public class FloorStruct {

	private String name;
	List<RoomStruct> rooms;
	
	public FloorStruct() {
		rooms = new ArrayList<RoomStruct>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RoomStruct> getRooms() {
		return rooms;
	}

	public void setRooms(List<RoomStruct> rooms) {
		this.rooms = rooms;
	}
}
