package it.unipr.ce.dsg.examples.chorddemonam;

import it.unipr.ce.dsg.examples.buildingfm.BuildingFunctionalModule;
import it.unipr.ce.dsg.examples.chordfm.ChordFunctionalModule;
import it.unipr.ce.dsg.examples.reasonerfm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.examples.sensorfm.SensorFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.TaskManagerFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

public class ChordDemoNam extends NetworkedAutonomicMachine {

	ChordFunctionalModule cfm = null;
	TaskManagerFunctionalModule tmfm = null;
	ReasonerFunctionalModule rfm = null;
	SensorFunctionalModule sfm = null;
	BuildingFunctionalModule bfm = null;

	public ChordFunctionalModule getCfm() {
		return cfm;
	}

	public void setCfm(ChordFunctionalModule cfm) {
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

	public ChordDemoNam(String configuration, String configPath) {
		super(10, "", 3, Platform.DESKTOP);

		this.setId("mccdemonam");

		cfm = new ChordFunctionalModule(this, configPath);
		this.addFunctionalModule(cfm);

		tmfm = new TaskManagerFunctionalModule(this);
		this.addFunctionalModule(tmfm);

		if (configuration.equals("LOOKUP")
				|| configuration.equals("BUILDINGLOOKUP")) {

			rfm = new ReasonerFunctionalModule(this);
			this.addFunctionalModule(rfm);

		} else if (configuration.equals("NOTIFICATION")) {

			sfm = new SensorFunctionalModule(this);
			this.addFunctionalModule(sfm);

		} else if (configuration.equals("BUILDINGNOTIFICATION")) {

			bfm = new BuildingFunctionalModule(this);
			this.addFunctionalModule(bfm);
		}

	}

	
	/* main function arguments:
	 * 
	 * args[0]: either NOTIFICATION / LOOKUP / BUILDINGNOTIFICATION /
	 * 			BUILDINGLOOKUP
	 * 
	 * args[1]: the configuration file for the Chord node
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
		
		ChordDemoNam mccdemonam = new ChordDemoNam(args[0], args[1]);

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

		} else if (args[0].equals("BUILDINGNOTIFICATION")) {

			mccdemonam.getBfm().startBuildingNotification();
			amiTask.addProcessingService("Publish");

		} else if (args[0].equals("BUILDINGLOOKUP")) {

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
