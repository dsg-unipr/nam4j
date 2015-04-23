package it.unipr.ce.dsg.examples.migration;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

public class TestFunctionalModuleForAndroid extends FunctionalModule {
	
	NetworkedAutonomicMachine nam;
	
	public TestFunctionalModuleForAndroid(NetworkedAutonomicMachine nam) {
		super(nam);
		this.setId("testfm");
		this.setName("TestFunctionalModule");
		this.nam = nam;
		System.out.println("\nI am " + this.getId() + " and I own to " + nam.getId());
	}
	
	public String printInfo() {
		return "\nI am printInfo method\n";
	}

	@Override
	public void addConsumableService(String id, IService service) {

	}

	@Override
	public void addProvidedService(String id, IService service) {

	}
}
