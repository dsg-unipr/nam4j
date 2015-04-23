package it.unipr.ce.dsg.examples.sensorfm;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

public class SensorFunctionalModule extends FunctionalModule {

	private SensorLogger sLogger = null;

	public SensorFunctionalModule(NetworkedAutonomicMachine nam) {
		super(nam);
		this.setId("sfm");
		this.setName("SensorFunctionalModule");
		this.sLogger = new SensorLogger("log/");
		sLogger.log("I am " + this.getId() + " and I own to " + nam.getId());
	}

	public SensorLogger getLogger() {
		return sLogger;
	}

	public void startTemperatureNotification(String buildingName,
			String floorName, String roomName, String sensorName,
			String sensorValue, String latitude, String longitude) {

		System.out
				.println("\n*************************************************************");
		System.out
				.println("I'm SensorFM and I'm starting to notify:"
						+ "\n* building: "
						+ buildingName
						+ "\n* floor: "
						+ floorName
						+ "\n* room: "
						+ roomName
						+ "\n* sensor: "
						+ sensorName
						+ "\n* value: " + sensorValue);
		System.out
				.println("*************************************************************");

		Thread t = new Thread(new ProvideTemperatureRunnable(this,
				buildingName, floorName, roomName, sensorName,
				sensorValue, latitude, longitude),
				"Provide sensor info thread");
		t.start();
		
	}

	@Override
	public void addConsumableService(String id, IService service) {
		
	}

	@Override
	public void addProvidedService(String id, IService service) {
		
	}
}
