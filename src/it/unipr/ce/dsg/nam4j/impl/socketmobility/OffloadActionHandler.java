package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.interfaces.IMobilityHandler;

public abstract class OffloadActionHandler implements IMobilityHandler, Runnable {
	
	public OffloadActionHandler() {
		
	}

	public void run() {
		// TODO
		
		// 1. tell the remote NAM that a FM is going to be offloaded;
		
		// 2. tell the local ServiceHandler associated to FM 
		// that FM is going to be offloaded to the remote NAM;
		
		// 3. move FM to the remote NAM;
		
		// 4. activate the ServiceHandler associated to F, in the remote NAM.
	}
}
