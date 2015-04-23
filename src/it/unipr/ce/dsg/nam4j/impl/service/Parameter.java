package it.unipr.ce.dsg.nam4j.impl.service;

import it.unipr.ce.dsg.nam4j.interfaces.IParameter;

/**
 * <p>
 * This class represents a parameter, 
 * which may be a service input or output, 
 * but in general any ontology entity.
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
 * 
 */

public class Parameter implements IParameter {

	String id = "param";
	String name = "Parameter";
	String value = "value";
	
	/**
     * constructor
     */
	public Parameter() {
	}
	
	/**
     * set the parameter name
     *
     * @param name
     */
	protected void setName(String name) {
		this.name = name;
	}
	
	/**
     * get the parameter name
     *
     * @return the name of the parameter
     */
	public String getName() {
		return this.name;
	}
	
	/**
     * set the parameter id
     *
     * @param id
     */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
     * get the parameter id
     *
     * @return the id of the parameter
     */
	public String getId() {
		return this.id;
	}
	
	/**
     * set the parameter value
     *
     * @param value
     */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
     * get the parameter value
     *
     * @return the value of the parameter
     */
	public String getValue() {
		return this.value;
	}
	
}
