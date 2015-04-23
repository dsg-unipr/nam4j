package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;

import java.util.HashMap;

/**
 * <p>
 * This interface represents a software entity 
 * which is able to provide context events.
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

public interface IContextProvider {
	
	/**
     * Adds a provided context event to the hashmap.
     *
     * @param id
     * @param contextEvent
     */
	void addProvidedContextEvent(String id, ContextEvent contextEvent);
	
	/**
     * Removes a provided context event, given its id in the hash map.
     *
     * @param id
     */
	void removeProvidedContextEvent(String id);
	
	/**
     * Returns the list of provided context events.
     *
     * @return the list of provided context events
     */
	HashMap<String, ContextEvent> getProvidedContextEvents();
	
	/**
     * Returns the provided context event, given its id in the hash map.
     *
     * @param id
     * @return the provided context event
     */
	ContextEvent getProvidedContextEvent(String id);
}
