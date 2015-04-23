package it.unipr.ce.dsg.gamidroid.ontology;

public class Location {
	
	private Building building;
	private Room room;
	private Floor floor;
	private Sensor sensor;
	private Latitude latitude;
	private Longitude longitude;

	public Location (){
		
		this.building = new Building();
		this.floor = new Floor();
		this.room = new Room();
		this.latitude = new Latitude();
		this.longitude = new Longitude();
		this.sensor = new Sensor();
		
		building.setValue("");
		floor.setValue("");
		room.setValue("");
		latitude.setValue("");
		longitude.setValue("");
		sensor.setValue("");
		sensor.setType("");
		
	}

	public Latitude getLatitude() {
		return latitude;
	}

	public void setLatitude(Latitude latitude) {
		this.latitude = latitude;
	}

	public Longitude getLongitude() {
		return longitude;
	}

	public void setLongitude(Longitude longitude) {
		this.longitude = longitude;
	}


	public Building getBuilding() {
		return building;
	}


	public void setBuilding(Building building) {
		this.building = building;
	}


	public Room getRoom() {
		return room;
	}


	public void setRoom(Room room) {
		this.room = room;
	}


	public Floor getFloor() {
		return floor;
	}


	public void setFloor(Floor floor) {
		this.floor = floor;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}
	
	
}
