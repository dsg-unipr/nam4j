package it.unipr.ce.dsg.gamidroid.sensorfm;

import it.unipr.ce.dsg.gamidroid.ontology.Building;
import it.unipr.ce.dsg.gamidroid.ontology.Floor;
import it.unipr.ce.dsg.gamidroid.ontology.Latitude;
import it.unipr.ce.dsg.gamidroid.ontology.Location;
import it.unipr.ce.dsg.gamidroid.ontology.Longitude;
import it.unipr.ce.dsg.gamidroid.ontology.Room;
import it.unipr.ce.dsg.gamidroid.ontology.Sensor;
import it.unipr.ce.dsg.gamidroid.ontology.Temperature;
import it.unipr.ce.dsg.gamidroid.ontology.TemperatureNotification;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.google.gson.Gson;

public class ProvideTemperatureRunnable implements Runnable {
	
	private SensorFunctionalModule sfm = null;
	// private String locationName = "nowhere"; /* The name of the location is not actually used */
	private String buildingName = "nowhere";
	private String floorName = "nowhere";
	private String roomName = "nowhere";
	private String sensorName = "nowhere";
	private String latitudeValue = "0";
	private String longitudeValue = "0";
	private String temperatureValue = "0";
	
	public ProvideTemperatureRunnable(SensorFunctionalModule sfm, 
			String locationName, 
			String temperatureValue) {
		this.sfm = sfm;
		// this.locationName = locationName;
		this.temperatureValue = temperatureValue;
	}
	
	public ProvideTemperatureRunnable(SensorFunctionalModule sfm, 
			String buildingName, 
			String floorName,
			String roomName,
			String sensorName,
			String temperatureValue,
			String latitude,
			String longitude) {
		
		this.sfm = sfm;
		this.buildingName = buildingName;
		this.floorName = floorName;
		this.roomName = roomName;
		this.sensorName = sensorName;
		this.temperatureValue = temperatureValue;
		this.latitudeValue = latitude;
		this.longitudeValue = longitude;
	}
	
	public void run() {
		
		/* Call publish service passing json message.
		 * Look into other functional modules, looking for requested service. */
		Collection<FunctionalModule> c = sfm.getNam().getFunctionalModules().values();
		Iterator<FunctionalModule> itr = c.iterator();
		String serviceName = null;
		FunctionalModule fm = null;
		FunctionalModule tempfm = null;
		
		while (itr.hasNext()) {
			tempfm = itr.next();
			if (tempfm.getName().equals(sfm.getName()))
				continue;
			
			Collection<IService> cc = tempfm.getProvidedServices().values();
			Iterator<IService> itrr = cc.iterator();
			
			while (itrr.hasNext()) {
				serviceName = itrr.next().getName();
				if (serviceName.equals("Publish")) {
					fm = tempfm;
				}
			}
		}

		while (true) {
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			TemperatureNotification tempNotif = new TemperatureNotification();
			
			Building building = new Building();
			building.setValue( buildingName );
			
			Floor floor = new Floor();
			floor.setValue( floorName );
			
			Room room = new Room();
			room.setValue(roomName);
			
			Sensor sensor = new Sensor();
			sensor.setValue( sensorName );
			
			Temperature temperature = new Temperature();
			temperature.setId("i21");
			temperature.setValue(this.temperatureValue);
			
			Latitude latitude = new Latitude ();
			latitude.setValue( latitudeValue );
			
			Longitude longitude = new Longitude();
			longitude.setValue( longitudeValue );
			
			Location location = new Location();
			location.setBuilding(building);
			location.setFloor( floor );
			location.setRoom(room);
			location.setSensor( sensor );
			location.setLatitude(latitude);
			location.setLongitude(longitude);
			
			Gson gson = new Gson();	
			
			Parameter parameter = new Parameter();
			parameter.setValue( gson.toJson( location ) );
			tempNotif.setLocation( parameter );
			
			tempNotif.setSubject(temperature);
			Date timestamp = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
			sfm.getLogger().log(df.format(timestamp));
			tempNotif.setTimestamp(df.format(timestamp));

			String json = gson.toJson(tempNotif);
			
			sfm.getLogger().log(tempNotif);
			
			/*
			 * fm is a ChordFunctionalModule and its "execute" method takes, as
			 * second parameter, the requested service which can be Join, Leave,
			 * Lookup, Publish, Subscribe.
			 */
			fm.execute(sfm.getId(), "Publish", json);
		}
	}
}
