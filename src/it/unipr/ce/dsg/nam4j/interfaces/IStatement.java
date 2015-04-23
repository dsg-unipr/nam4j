package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.service.Parameter;

/**
 * <p>
 * This interface represents a statement, i.e., a tuple
 * (subject, action, object, location)
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

public interface IStatement {

	/**
     * get the Subject name.
     *
     * @return the name of the Subject.
     */
	String getName();
	
	/**
     * set the Subject id.
     *
     * @param id.
     */
	void setId(String id);
	
	/**
     * get the Subject id.
     *
     * @return the id of the Subject.
     */
	String getId();
	
	/**
     * get the Subject Content.
     *
     * @return the content of the Subject.
     */
	 Parameter getSubject();
	
	/**
     * set the Subject Content.
     *
     * @param subject.
     */
	void setSubject(Parameter subject);
	
	/**
     * get the Object Content.
     *
     * @return the content of the Object.
     */
	Parameter getObject();
	
	/**
     * set the Object Content.
     *
     * @param object.
     */
	void setObject(Parameter object);
	
	/**
     * get the Location Content.
     *
     * @return the content of the Location.
     */
	Parameter getLocation();
	
	/**
     * set the Location Content.
     *
     * @param location.
     */
	void setLocation(Parameter location);
	
	/**
     * get the Action Content.
     *
     * @return the content of the Action.
     */
	Parameter getAction();
	
	/**
     * set the Action Content.
     *
     * @param action.
     */
	void setAction(Parameter action);
}
