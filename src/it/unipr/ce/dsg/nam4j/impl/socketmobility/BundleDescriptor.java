
package it.unipr.ce.dsg.nam4j.impl.socketmobility;

import it.unipr.ce.dsg.nam4j.interfaces.IBundleDescriptor;

/**
 * <p>
 * This class represents a software bundle, which may be either
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
public class BundleDescriptor implements IBundleDescriptor {

	private static final long serialVersionUID = 1L;
	
	/**
	 * File name of the Functional Module jar or of the Service java file
	 */
	String fileName;
	
	/**
	 *  Name of the functional module main class or name of the service class
	 */
	String mainClassName;
	
	/**
	 * String containing the package and the class names
	 */
	String completeName;
	
	/**
     * Returns the name of the file describing the functional module or the service.
     *
     * @return the name of the file describing the functional module or the service
     */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
     * Returns the name of the main class of the functional module or the service.
     *
     * @return the name of the main class of the functional module or the service
     */
	public String getMainClassName() {
		return this.mainClassName;
	}
	
	/**
     * Returns the complete name of the main class of the functional module or the service.
     *
     * @return the complete name of the main class of the functional module or the service
     */
	public String getCompleteName() {
		return this.completeName;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param f the name of the described file
	 * @param m the name of the described functional module or service main class
	 * @param c the complete name of the described functional module or service main class
	 */
	public BundleDescriptor(String f, String m, String c){  
		this.fileName = f;  
		this.mainClassName = m;
		this.completeName = c;
	}
}