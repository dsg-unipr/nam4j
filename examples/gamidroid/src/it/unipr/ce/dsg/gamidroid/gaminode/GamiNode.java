package it.unipr.ce.dsg.gamidroid.gaminode;

import it.unipr.ce.dsg.gamidroid.buildingfm.BuildingFunctionalModule;
import it.unipr.ce.dsg.gamidroid.centralizednetworkfm.CentralizedFunctionalModule;
import it.unipr.ce.dsg.gamidroid.chordfm.ChordFunctionalModule;
import it.unipr.ce.dsg.gamidroid.reasonerfm.ReasonerFunctionalModule;
import it.unipr.ce.dsg.gamidroid.sensorfm.SensorFunctionalModule;
import it.unipr.ce.dsg.gamidroid.taskmanagerfm.TaskManagerFunctionalModule;
import it.unipr.ce.dsg.gamidroid.taskmanagerfm.UPCPFTaskDescriptor;
import it.unipr.ce.dsg.gamidroid.utils.Constants;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.s2p.centralized.interfaces.IEventListener;
import it.unipr.ce.dsg.s2pchord.msg.MessageListener;
import it.unipr.ce.dsg.s2pchord.resource.ResourceListener;

import java.io.File;

import org.w3c.dom.Document;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class GamiNode extends NetworkedAutonomicMachine {

	private static Context mContext;
	public static String TAG = "AndroidGamiNode";
	private static GamiNode androidGamiNode;
	private TaskManagerFunctionalModule tmfm = null;
	private ReasonerFunctionalModule rfm = null;
	private static ChordFunctionalModule cfm = null;
	private static BuildingFunctionalModule bfm = null;
	private static SensorFunctionalModule sfm = null;
	private static CentralizedFunctionalModule centralizedfm = null;

	private static NetworkedAutonomicMachine thisNam;

	private static String pathToSaveFile = Environment.getExternalStorageDirectory().toString() + "/";

	private GamiNode(Context mContext, String configuration, String confFile) {
		super(10, pathToSaveFile, 3);

		this.setId("gaminode");

		thisNam = this;

		this.mContext = mContext;

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK,
				"");

		if (currentNetwork.equalsIgnoreCase(Constants.CHORD)) {
			
			cfm = new ChordFunctionalModule(this);
			this.addFunctionalModule(cfm);
			
		} else if (currentNetwork.equalsIgnoreCase(Constants.MESH)) {

			File sdCache = new File(Environment.getExternalStorageDirectory()
					+ Constants.CONFIGURATION_FILES_PATH);

			/*
			 * Opening configuration file created by class
			 * it.unipr.ce.dsg.gamidroid.android.FileManager
			 */
			File configFile = new File(sdCache,
					Constants.PEER_CONFIGURATION_FILE_NAME);

			String configFilePath = configFile.getAbsolutePath();
			
			centralizedfm = new CentralizedFunctionalModule(thisNam,
					configFilePath, "NAM");

			this.addFunctionalModule(centralizedfm);
		}

		tmfm = new TaskManagerFunctionalModule(this);
		this.addFunctionalModule(tmfm);

		if (configuration.equals("LOOKUP")) {
			rfm = new ReasonerFunctionalModule(this);
			this.addFunctionalModule(rfm);
		}
	}

	/**
	 * Listener for Chord resource receiving.
	 * 
	 * @param rl
	 * 			An object of a class implementing {@link ResourceListener} interface
	 */
	public static void addChordResourceListener(ResourceListener rl) {
		cfm.addResourceListener(rl);
	}
	
	/**
	 * Listener for Mesh resource receiving.
	 * 
	 * @param el
	 * 			An object of a class implementing {@link IEventListener} interface
	 */
	public static void addMeshResourceListener(IEventListener eventListener) {
		centralizedfm.addEventListener(eventListener);
	}

	/**
	 * Listener for Chord message receiving.
	 * 
	 * @param rl
	 * 			An object of a class implementing {@link MessageListener} interface
	 */
	public static void addChordMessageListener(MessageListener ml) {
		cfm.addMessageListener(ml);

	}
	
	/**
	 * Listener for Mesh message receiving.
	 * 
	 * @param el
	 * 			An object of a class implementing {@link IEventListener} interface
	 */
	public static void addMeshMessageListener(IEventListener el) {
		
		//TODO: add the resource listeners for Centralized network
			
	}

	/**
	 * Method to leave the network
	 */
	public static void disconnect() {

		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				Constants.PREFERENCES, Context.MODE_PRIVATE);
		String currentNetwork = sharedPreferences.getString(Constants.NETWORK, "");

		if (currentNetwork.equalsIgnoreCase(Constants.CHORD)) {
			cfm.disconnect();
		} else if (currentNetwork.equalsIgnoreCase(Constants.MESH)) {
			centralizedfm.disconnect();
		}
		
		
		androidGamiNode = null;
	}

	public ChordFunctionalModule getCfm() {
		return cfm;
	}

	public void setCfm(ChordFunctionalModule cfm) {
		GamiNode.cfm = cfm;
	}

	public static CentralizedFunctionalModule getCentralizedfm() {
		return centralizedfm;
	}

	public static void setCentralizedfm(CentralizedFunctionalModule centralizedfm) {
		GamiNode.centralizedfm = centralizedfm;
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

	public static GamiNode getAndroidGamiNode(Context mContext) {
		if (androidGamiNode == null) {
			androidGamiNode = new GamiNode(mContext, "LOOKUP", null);
		}
		return androidGamiNode;
	}

	public static void publishBuilding(Document xml) {

		UPCPFTaskDescriptor amiTask = new UPCPFTaskDescriptor("AmITask", "T1");
		amiTask.setState("UNSTARTED");
		bfm = new BuildingFunctionalModule(thisNam);
		thisNam.addFunctionalModule(bfm);

		bfm.startBuildingNotificationFromMobile(xml);
		amiTask.addProcessingService("Publish");
	}

	public static void publishSensor(String address, String lat, String lng,
			String name, String floor, String room, String value) {

		UPCPFTaskDescriptor amiTask = new UPCPFTaskDescriptor("AmITask", "T1");
		amiTask.setState("UNSTARTED");
		sfm = new SensorFunctionalModule(thisNam);
		thisNam.addFunctionalModule(sfm);

		sfm.startTemperatureNotification(address, floor, room, name, value,
				lat, lng);
		amiTask.addProcessingService("Publish");
	}

}
