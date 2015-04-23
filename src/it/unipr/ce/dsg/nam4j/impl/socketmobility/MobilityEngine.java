package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import java.util.ArrayList;

import it.unipr.ce.dsg.nam4j.interfaces.IMobilityEngine;
import it.unipr.ce.dsg.nam4j.interfaces.IResourceDescriptor;

public abstract class MobilityEngine implements IMobilityEngine, Runnable {
	
	public boolean checkMobilityConditions(
			ArrayList<IResourceDescriptor> resources) {
		// TODO 
		
		// check resources to decide whether FM mobility is necessary or not
		
		return false;
	}

	public void run() {
		// TODO 
		
		// periodically call checkMobilityConditions()
		
		// if true, perform one or more mobility actions, by means of mobility handlers
		// (e.g., OffloadActionhandler)
	}
}
