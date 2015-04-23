package it.unipr.ce.dsg.nam4j.interfaces;


/**
 * <p>
 * This interface represents a service request dispatcher.
 * </p>
 * 
 * <p>
 *  Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 *  Permission is granted to copy, distribute and/or modify this document
 *  under the terms of the GNU Free Documentation License, Version 1.3
 *  or any later version published by the Free Software Foundation;
 *  with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
 *  A copy of the license is included in the section entitled "GNU
 *  Free Documentation License".
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */
public interface IDispatcher {

	/**
     * Search for a local functional module that exposes the requested service 
     *
     * @param requestedService
     * @return the functional module that exposes the requested service (may be null)
     */
	public IFunctionalModule searchForLocalFunctionalModule(IService requestedService);
	
	/**
	 * Search for a remote functional module that exposes the requested service
	 * 
	 * @param requestedService
	 * @return the functional module that exposes the requested service (may be null)
	 */
	public IFunctionalModule searchForRemoteFunctionalModule(IService requestedService);
	
	/**
	 * Assign a service request to a functional module
	 * 
	 * @param serviceRequest
	 * @param functionalModule
	 * @return a status indicator
	 */
	public int assignServiceRequest(IService serviceRequest, 
			IFunctionalModule functionalModule);
}
