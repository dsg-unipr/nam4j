package it.unipr.ce.dsg.nam4j.impl.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.interfaces.IDispatcher;
import it.unipr.ce.dsg.nam4j.interfaces.IFunctionalModule;
import it.unipr.ce.dsg.nam4j.interfaces.IService;
import it.unipr.ce.dsg.nam4j.interfaces.IServiceRequest;

public abstract class Dispatcher implements IDispatcher {
	
	/**
	 * NAM associated to the dispatcher
	 */
	NetworkedAutonomicMachine nam = null;
	
	/**
	 * The service request queue
	 */
	PriorityBlockingQueue<IServiceRequest> serviceRequestQueue = new PriorityBlockingQueue<IServiceRequest>();
	
	/**
	 * The threads pool to manage the incoming service requests.
	 */
	ExecutorService requestHandlingPool;

	/**
	 * Default constructor
	 * @param nam
	 */
	public Dispatcher(NetworkedAutonomicMachine nam) {
		this.nam = nam;
	}
	
	/**
     * Search for a local functional module that exposes the requested service 
     *
     * @param requestedService
     * @return the functional module that exposes the requested service (may be null)
     */
	public IFunctionalModule searchForLocalFunctionalModule(
			IService requestedService) {
		// TODO 
		
		// Get the list of local FMs, from this.nam
		
		// Look for a suitable FM (see SearchTemperatureRunnable, for example)
		
		// if suitable FM exists, return it
		
		return null;
	}

	/**
	 * Search for a remote functional module that exposes the requested service
	 * 
	 * @param requestedService
	 * @return the functional module that exposes the requested service (may be null)
	 */
	public IFunctionalModule searchForRemoteFunctionalModule(
			IService requestedService) {
		// TODO 
		
		// If requestor is the local nam (check name and address in ServiceRequest), 
		// send request to remote nams' Dispatchers
		
		// if suitable FM exists, return it
		
		return null;
	}

	/**
	 * Assign a service request to a functional module
	 * 
	 * @param serviceRequest
	 * @param functionalModule
	 * @return a status indicator
	 */
	public int assignServiceRequest(IService serviceRequest,
			IFunctionalModule functionalModule) {
		// TODO 
		
		// if functionalModule is local, then ...
		
		// else if functionalModule is remote, then ...
		
		return 0;
	}

}
