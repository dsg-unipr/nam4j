package it.unipr.ce.dsg.examples.centralizeddemonam;

import it.unipr.ce.dsg.examples.buildingfm.BuildingFunctionalModule;
import it.unipr.ce.dsg.examples.centralizednetworkfm.CentralizedFunctionalModule;
import it.unipr.ce.dsg.examples.reasonerfm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.examples.sensorfm.SensorFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.TaskManagerFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

public class CentralizedDemoNam extends NetworkedAutonomicMachine {
	
	CentralizedFunctionalModule cfm = null;
	TaskManagerFunctionalModule tmfm = null;
	ReasonerFunctionalModule rfm = null;
	SensorFunctionalModule sfm = null;
	BuildingFunctionalModule bfm = null;
	
	public CentralizedFunctionalModule getCfm() {
		return cfm;
	}

	public void setKfm(CentralizedFunctionalModule cfm) {
		this.cfm = cfm;
	}

	public TaskManagerFunctionalModule getTmfm() {
		return tmfm;
	}

	public void setTmfm(TaskManagerFunctionalModule tmfm) {
		this.tmfm = tmfm;
	}

	public ReasonerFunctionalModule getRfm() {
		return rfm;
	}

	public void setRfm(ReasonerFunctionalModule rfm) {
		this.rfm = rfm;
	}

	public SensorFunctionalModule getSfm() {
		return sfm;
	}

	public void setSfm(SensorFunctionalModule sfm) {
		this.sfm = sfm;
	}
	
	public CentralizedDemoNam(String configuration, String configPath) {
		super(10, "", 3);
		
		this.setId("mccdemonam");
		
		// CentralizedFunctionalModule creates the centralized network peer object and joins the network  
		cfm = new CentralizedFunctionalModule(this, configPath);
		this.addFunctionalModule(cfm);

		tmfm = new TaskManagerFunctionalModule(this);
		this.addFunctionalModule(tmfm);
		
		if (configuration.equals("LOOKUP") || configuration.equals("BUILDING_LOOKUP")) {

			rfm = new ReasonerFunctionalModule(this);
			this.addFunctionalModule(rfm);

		} else if (configuration.equals("NOTIFICATION")) {

			sfm = new SensorFunctionalModule(this);
			this.addFunctionalModule(sfm);

		} else if (configuration.equals("BUILDING_NOTIFICATION")) {

			bfm = new BuildingFunctionalModule(this);
			this.addFunctionalModule(bfm);
		}
	}
	
	/* Main function arguments:
	 * 
	 * args[0]: either NOTIFICATION / LOOKUP / BUILDING_NOTIFICATION / BUILDING_LOOKUP
	 * 
	 * args[1]: the configuration file for the Mesh network node
	 * 
	 * args[2]: a building address
	 * 
	 * args[3]: a floor name
	 * 
	 * args[4]: a room name
	 * 
	 * args[5]: a sensor name
	 * 
	 * args[6]: the sensor value
	 * 
	 * args[7]: a latitude value
	 * 
	 * args[8]: a longitude value
	 * 
	 */
	public static void main(String[] args) {
		
		CentralizedDemoNam mccdemonam = new CentralizedDemoNam(args[0], args[1]);

		UPCPFTaskDescriptor amiTask = new UPCPFTaskDescriptor("AmITask", "T1");
		amiTask.setState("UNSTARTED");

		if (args[0].equals("NOTIFICATION")) {
			
			mccdemonam.getSfm().startTemperatureNotification(args[2], args[3],
					args[4], args[5], args[6], args[7], args[8]);
			amiTask.addProcessingService("Publish");

		} else if (args[0].equals("LOOKUP")) {

			mccdemonam.getRfm().startTemperatureNotificationLookup(args[2],
					args[3], args[4], args[5]);
			amiTask.addProcessingService("Lookup");

		} else if (args[0].equals("BUILDING_NOTIFICATION")) {

			mccdemonam.getBfm().startBuildingNotification();
			amiTask.addProcessingService("Publish");

		} else if (args[0].equals("BUILDING_LOOKUP")) {

			mccdemonam.getRfm().startBuildingNotificationLookup(args[2]);
			amiTask.addProcessingService("Lookup");
		}

		mccdemonam.getTmfm().addTaskDescriptor(amiTask);
		mccdemonam.getTmfm().startTaskManagement();
	}

	public BuildingFunctionalModule getBfm() {
		return bfm;
	}

	public void setBfm(BuildingFunctionalModule bfm) {
		this.bfm = bfm;
	}
}
