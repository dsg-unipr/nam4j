package it.unipr.ce.dsg.gamidroid.buildingfm;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import org.w3c.dom.Document;

public class BuildingFunctionalModule extends FunctionalModule {

	public BuildingFunctionalModule(NetworkedAutonomicMachine nam) {
		super(nam);
		this.setId("bfm");
		this.setName("BuildingFunctionalModule");

	}

	/**
	 * Creation of a thread that publishes the structure of the building in the
	 * network. The structure has to be defined in an .xml file.
	 */
	public void startBuildingNotification() {
		Thread t = new Thread(new ProvideBuildingRunnable(this),
				"Provide Building thread");
		t.start();
	}

	/**
	 * Creation of a thread that publishes the structure of the building in the
	 * network.
	 * 
	 * @param xml
	 *            An xml Document containing the building structure
	 */
	public void startBuildingNotificationFromMobile(Document xml) {
		Thread t = new Thread(new ProvideBuildingRunnable(this, xml),
				"Provide Building thread");
		t.start();
	}

	@Override
	public void addConsumableService(String id, IService service) {}

	@Override
	public void addProvidedService(String id, IService service) {}
}
