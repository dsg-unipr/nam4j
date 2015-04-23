package it.unipr.ce.dsg.nam4j.interfaces;

import java.io.Serializable;

/**
 * <p>
 * This interface represents a software bundle, which may be either
 * a functional module or a service.
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
public interface IBundleDescriptor extends Serializable {

	/**
     * Returns the name of the file describing the functional module or the service.
     *
     * @return the name of the file describing the functional module or the service
     */
	String getFileName();
	
	/**
     * Returns the name of the main class of the functional module or the service.
     *
     * @return the name of the main class of the functional module or the service
     */
	String getMainClassName();
	
	/**
     * Returns the complete name of the main class of the functional module or the service.
     *
     * @return the complete name of the main class of the functional module or the service
     */
	String getCompleteName();
	
}
