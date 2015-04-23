package it.unipr.ce.dsg.examples.galileo.fm;

import it.unipr.ce.dsg.example.galileo.service.ReadTemperatureSensorService;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

public class SensorFunctionalModule extends FunctionalModule {

	private SensorFunctionalModule sfm;
	private String[] args;
	
	public SensorFunctionalModule(NetworkedAutonomicMachine nam, String[] args) {
		super(nam);
		this.setId("sensorFM");
		this.setName("SensorFunctionalModule");
		
		this.sfm = this;
		this.args = args;

		System.out.println("\nI am " + this.getId() + " and I own to "
				+ nam.getId());
	}
	
	public void startTemperaturePublishingService() {
		ReadTemperatureSensorService rds = new ReadTemperatureSensorService(args, sfm);
		this.sfm.addProvidedService(rds.getId(), rds);
	}

	@Override
	public void addConsumableService(String id, IService service) {

	}

	@Override
	public void addProvidedService(String id, IService service) {

	}
	
	

}
