package it.unipr.ce.dsg.nam4j.interfaces;

import java.util.HashMap;

/**
 * <p>
 * This interface represents a software entity 
 * which is able to provide services.
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

public interface IServiceProvider {

	/**
     * Adds a provided service to the hashmap.
     * 
     * @param id
     * @param service.
     */
	void addProvidedService(String id, IService service);
	
	/**
     * Removes a provided service, given its id in the hash map.
     *
     * @param id.
     */
	void removeProvidedService(String id);
	
	/**
     * Returns the list of provided services.
     *
     * @return the list of provided services
     */
	HashMap<String, IService> getProvidedServices();
	
	/**
     * Returns the provided service, given its id in the hash map.
     *
     * @param id
     * @return the provided service
     */
	IService getProvidedService(String id);
	
}
