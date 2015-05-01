package it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser;

/**
 * <p>
 * Class used by {@link SAXPArser} to parse the requirements element in the XML
 * file which describes an item (a {@link FunctionalModule} or a {@link Service}).
 * Such an element lists minimum system requirements for the execution.
 * </p>
 * 
 * <p>
 * Copyright (c) 2014, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class MinimumRequirements {

	/** The minimum number of cores */
	private int numProcessors = 0;
	
	/** The minimum clock frequency in Hertz */
	private int clockFrequency = 0;

	/** The minimum required amount of memory in MegaBytes */
	private int ram = 0;

	/** The minimum required amount of storage space in MegaBytes */
	private int storage = 0;

	/** Whether a network access is required */
	private boolean networkRequested = false;

	/** Whether location sensors are required */
	private boolean locationSensorRequested = false;
	
	/** Whether a camera is required */
	private boolean cameraRequested = false;

	/**
	 * Class constructor.
	 */
	public MinimumRequirements() {}

	public int getNumProcessors() {
		return numProcessors;
	}

	public void setNumProcessors(int numProcessors) {
		this.numProcessors = numProcessors;
	}

	public int getClockFrequency() {
		return clockFrequency;
	}

	public void setClockFrequency(int clockFrequency) {
		this.clockFrequency = clockFrequency;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public boolean isNetworkRequested() {
		return networkRequested;
	}

	public void setNetworkRequested(boolean networkAvailability) {
		this.networkRequested = networkAvailability;
	}

	public boolean isLocationSensorRequested() {
		return locationSensorRequested;
	}

	public void setLocationSensorRequested(boolean locationSensorAvailability) {
		this.locationSensorRequested = locationSensorAvailability;
	}

	public boolean isCameraRequested() {
		return cameraRequested;
	}

	public void setCameraRequested(boolean cameraAvailability) {
		this.cameraRequested = cameraAvailability;
	}

}
