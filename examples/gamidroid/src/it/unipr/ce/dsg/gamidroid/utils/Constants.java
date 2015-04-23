package it.unipr.ce.dsg.gamidroid.utils;

public class Constants {
	
	/* Shared Preferences */
	public static final String PREFERENCES = "SHARED_PREFERENCES";
	public static String NETWORK = "NETWORK";
	public static final String DEFAULT_NETWORK = "Mesh";
	
	/* Networks */
	public static final String MESH = "Mesh";
	public static final String CHORD = "Chord";
	
	/* Constants representing the reason a resource has been received */
	public static final String reasonResearched = "RESEARCHED";
	public static final String reasonAssigned = "ASSIGNED";
	
	/* Peer configuration */
	public static final String PEER_CONFIGURATION_FILE_NAME = "config.cfg";
	public static final String BOOTSTRAP_CONFIGURATION_FILE_NAME = "bs.cfg";
	public static final String CONFIGURATION_FILES_PATH = "/Android/data/it.unipr.ce.dsg.gamidroid/cache/";

	/* Default bootstrap configuration */
	public static final String DEFAULT_BOOTSTRAP_ADDRESS = "192.168.56.1";
	public static final String DEFAULT_BOOTSTRAP_PORT = "5080";
	
	/* Google Maps */
	public static final String  GEOCODER_ADDRESS = "http://maps.googleapis.com/maps/api/geocode/json?address=";
	
	/* Tag for the cpu and mem usage message received from DeviceMonitorService */
	public static final String RECEIVED_RESOURCES_UPDATE = "RECEIVED_RESOURCES_UPDATE";
}
