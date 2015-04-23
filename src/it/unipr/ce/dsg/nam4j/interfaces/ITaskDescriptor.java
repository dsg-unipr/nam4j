package it.unipr.ce.dsg.nam4j.interfaces;

import java.util.ArrayList;

/**
 * <p>
 * This interface represents a task descriptor.
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

public interface ITaskDescriptor {

	/**
     * set the task name
     *
     * @param name
     */
	void setName(String name);
	
	/**
     * get the task name
     *
     * @return the name of the task
     */
	String getName();
	
	/**
     * set the task id
     *
     * @param id
     */
	void setId(String id);
	
	/**
     * get the task id
     *
     * @return the id of the task
     */
	String getId();
	
	/**
     * set the task state
     *
     * @param state
     */
	void setState(String state);
	
	/**
     * get the task state
     *
     * @return the state of the task
     */
	String getState();

	/**
     * set the list of allowed states
     *
     * @param allowedStates
     */
	public void setAllowedStates(ArrayList<String> allowedStates);
	
	/**
     * get the list of allowed states
     *
     * @return the list of allowed states
     */
	public ArrayList<String> getAllowedStates();
	
}
