package it.unipr.ce.dsg.examples.kademliademonam;

import it.unipr.ce.dsg.examples.buildingfm.BuildingFunctionalModule;
import it.unipr.ce.dsg.examples.kademliafm.KademliaFunctionalModule;
import it.unipr.ce.dsg.examples.reasonerfm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.examples.sensorfm.SensorFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.TaskManagerFunctionalModule;
import it.unipr.ce.dsg.examples.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;

public class KademliaDemoNam extends NetworkedAutonomicMachine {

	KademliaFunctionalModule kfm = null;
	TaskManagerFunctionalModule tmfm = null;
	ReasonerFunctionalModule rfm = null;
	SensorFunctionalModule sfm = null;
	BuildingFunctionalModule bfm = null;

	public KademliaFunctionalModule getKfm() {
		return kfm;
	}

	public void setKfm(KademliaFunctionalModule kfm) {
		this.kfm = kfm;
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

	public KademliaDemoNam(String configuration, String configPath) {
		super(10, "", 3);

		this.setId("mccdemonam");

		kfm = new KademliaFunctionalModule(this, configPath);
		this.addFunctionalModule(kfm);

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
	 * args[1]: the configuration file for the Kademlia node
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
		
		KademliaDemoNam mccdemonam = new KademliaDemoNam(args[0], args[1]);

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
