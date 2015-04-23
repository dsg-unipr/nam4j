package it.unipr.ce.dsg.nam4j.interfaces;

/**
 * <p>
 * This interface represents the effect of a service.
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
 * @author Marco Muro
 * 
 */

public interface IEffect {

	/**
     * set the effect name
     *
     * @param name
     */
	void setName(String name);
	
	/**
     * get the effect name
     *
     * @return the name of the effect
     */
	String getName();
	
	/**
     * set the effect id
     *
     * @param id
     */
	void setId(String id);
	
	/**
     * get the effect id
     *
     * @return the id of the effect
     */
	String getId();
}
