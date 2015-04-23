package it.unipr.ce.dsg.examples.galileo.fm;

import it.unipr.ce.dsg.examples.ontology.Building;
import it.unipr.ce.dsg.examples.ontology.Floor;
import it.unipr.ce.dsg.examples.ontology.Location;
import it.unipr.ce.dsg.examples.ontology.Notify;
import it.unipr.ce.dsg.examples.ontology.Room;
import it.unipr.ce.dsg.examples.ontology.Sensor;
import it.unipr.ce.dsg.examples.ontology.Temperature;
import it.unipr.ce.dsg.examples.ontology.TemperatureNotification;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.gson.Gson;

public class ReasonerFunctionalModule extends FunctionalModule {
	private String[] args;
	private ReasonerLogger rLogger = null;
	
	private HashMap<String, Date> locationMap = null;
	
	private SearchTemperatureRunnable str;

	public ReasonerFunctionalModule(NetworkedAutonomicMachine nam, String[] args) {
		super(nam);
		this.setId("reasonerFM");
		this.setName("reasonerFunctionalModule");
		this.rLogger = new ReasonerLogger("log/");
		rLogger.log("I am " + this.getId() + " and I own to " + nam.getId());
		this.args = args;

		System.out.println("\nI am " + this.getId() + " and I own to "
				+ nam.getId());
		
		this.locationMap = new HashMap<String, Date>();

		Notify notifyService = new Notify();
		notifyService.setId("s1");
		this.addProvidedService(notifyService.getId(), notifyService);

		// create Context objects
		// and add to providedContextEvents and consumableContextEvents
		Temperature temperature = new Temperature();
		TemperatureNotification tempNotif = new TemperatureNotification();
		tempNotif.setObject(temperature);
		this.addProvidedContextEvent("c1", tempNotif);
	}

	public void startTemperatureNotificationLookup() {

		System.out
				.println("\n*************************************************************");
		System.out
				.println("I'm ReasonerFM and I'm starting the lookup for:\n* building: "
						+ args[3]
						+ "\n* floor: "
						+ args[4]
						+ "\n* room: "
						+ args[5] + "\n* sensor: " + args[6]);
		System.out
				.println("*************************************************************");

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date initDay = null;

		try {
			initDay = df.parse("25/12/2010 00:00:00");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		if (str != null) {
			str.stopThread();
			locationMap.clear();
		}

		Location locationSensorSearch;
		Gson gson = new Gson();

		locationSensorSearch = new Location();
		locationSensorSearch.setBuilding(new Building(args[3]));
		locationSensorSearch.setFloor(new Floor(args[4]));
		locationSensorSearch.setRoom(new Room(args[5]));
		locationSensorSearch.setSensor(new Sensor(args[6]));

		String locationString = gson.toJson(locationSensorSearch);

		locationMap.put(locationString, initDay);

		// create and start a thread that periodically looks up for Temperature
		// notification events
		
		Thread t = new Thread(new SearchTemperatureRunnable(this),
				"Search temperature thread");
		t.start();

	}
	
	public ReasonerLogger getLogger() {
		return rLogger;
	}
	
	public HashMap<String, Date> getLocationMap() {
		return locationMap;
	}

	public void setLocationMap(HashMap<String, Date> locationMap) {
		this.locationMap = locationMap;
	}
	
	public void stopTemperatureNotificationLookup() {
		str.stopThread();
	}

	@Override
	public void addConsumableService(String id, IService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProvidedService(String id, IService service) {
		// TODO Auto-generated method stub
		
	}

}
