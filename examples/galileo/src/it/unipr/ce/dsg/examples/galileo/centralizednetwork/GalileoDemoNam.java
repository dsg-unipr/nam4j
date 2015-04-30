package it.unipr.ce.dsg.examples.galileo.centralizednetwork;

import it.unipr.ce.dsg.examples.buildingfm.BuildingFunctionalModule;
import it.unipr.ce.dsg.examples.centralizednetworkfm.CentralizedFunctionalModule;
import it.unipr.ce.dsg.examples.galileo.fm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.examples.galileo.fm.SensorFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.TaskManagerFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

public class GalileoDemoNam extends NetworkedAutonomicMachine {
	
	CentralizedFunctionalModule cfm = null;
	TaskManagerFunctionalModule tmfm = null;
	ReasonerFunctionalModule rfm = null;
	SensorFunctionalModule srfm = null;
	BuildingFunctionalModule bfm = null;

	public CentralizedFunctionalModule getCfm() {
		return cfm;
	}

	public void setCfm(CentralizedFunctionalModule cfm) {
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
		return srfm;
	}

	public void setSfm(SensorFunctionalModule srfm) {
		this.srfm = srfm;
	}

	public GalileoDemoNam(String[] args) {
		super(10, "", 3, Platform.DESKTOP);

		this.setId("galileoMccDemonam");

		String configuration = args[0];

		cfm = new CentralizedFunctionalModule(this, args[1]);
		this.addFunctionalModule(cfm);

		tmfm = new TaskManagerFunctionalModule(this);
		this.addFunctionalModule(tmfm);

		/*
		 * if (configuration.equals("LOOKUP") ||
		 * configuration.equals("BUILDINGLOOKUP")) {
		 * 
		 * rfm = new ReasonerFunctionalModule(this);
		 * this.addFunctionalModule(rfm);
		 * 
		 * } else
		 */

		if (configuration.equals("GALILEO_NOTIFICATION")) {

			srfm = new SensorFunctionalModule(this, args);
			this.addFunctionalModule(srfm);

		} else if (configuration.equals("GALILEO_LOOKUP")) {

			rfm = new ReasonerFunctionalModule(this, args);
			this.addFunctionalModule(rfm);

		}

		/*
		 * else if (configuration.equals("BUILDINGNOTIFICATION")) {
		 * 
		 * bfm = new BuildingFunctionalModule(this);
		 * this.addFunctionalModule(bfm); }
		 */

	}

	/*
	 * Main function arguments:
	 * 
	 * args[0]: either GALILEO_NOTIFICATION / GALILEO_LOOKUP
	 * 
	 * args[1]: configuration file path
	 * 
	 * args[2]: a building address
	 * 
	 * args[3]: a floor name
	 * 
	 * args[4]: a room name
	 * 
	 * args[5]: a sensor name
	 * 
	 * args[6]: a latitude value
	 * 
	 * args[7]: a longitude value
	 */
	public static void main(String[] args) {

		GalileoDemoNam mccdemonam = new GalileoDemoNam(args);

		UPCPFTaskDescriptor amiTask = new UPCPFTaskDescriptor("AmITask", "T1");
		amiTask.setState("UNSTARTED");

		if (args[0].equals("GALILEO_NOTIFICATION")) {

			mccdemonam.getSfm().startTemperaturePublishingService();
			amiTask.addProcessingService("Publish");

		} else if (args[0].equals("GALILEO_LOOKUP")) {

			mccdemonam.getRfm().startTemperatureNotificationLookup();
			amiTask.addProcessingService("Lookup");

		}

		 /* else if (args[0].equals("BUILDINGNOTIFICATION")) {
		 * 
		 * mccdemonam.getBfm().startBuildingNotification();
		 * amiTask.addProcessingService("Publish");
		 * 
		 * } else if (args[0].equals("BUILDINGLOOKUP")) {
		 * 
		 * mccdemonam.getRfm().startBuildingNotificationLookup(args[1]);
		 * amiTask.addProcessingService("Lookup"); }
		 */

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
