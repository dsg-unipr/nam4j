package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.service.Effect;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.impl.service.Precondition;

import java.util.HashMap;

/**
 * <p>
 * This interface represents a service.
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

public interface IService {

	/**
     * get the service name
     *
     * @return the name of the service
     */
	String getName();
	
	/**
     * set the service id
     *
     * @param id
     */
	void setId(String id);
	
	/**
     * get the service id
     *
     * @return the id of the service
     */
	String getId();
	
	/**
     * get the input list of the service
     *
     * @return the list of inputs
     */
	HashMap<String, Parameter> getInputs();
	
	/**
     * get the input of the service by id
     *
     * @param id
     * @return the input
     */
	Parameter getInput(String id);
	
	/**
     * add an input to the service
     *
     *@param input
     */
	void addInput(Parameter input);
	
	/**
     * remove input from service by id
     *
     *@param id
     */
	void removeInput(String id);
	
	/**
     * get the output list of the service
     *
     * @return the list of outputs
     */
	HashMap<String, Parameter> getOutputs();
	
	/**
     * get the output of the service by id
     *
     * @param id
     * @return the output
     */
	Parameter getOutput(String id);
	
	/**
     * add output to service
     *
     *@param output
     */
	void addOutput(Parameter output);
	
	/**
     * remove output from service by id
     *
     *@param id
     */
	void removeOutput(String id);
	
	/**
     * get the precondition list of the service
     *
     * @return the list of preconditions
     */
	HashMap<String, Precondition> getPreconditions();
	
	/**
     * get a precondition of the service by id
     *
     * @param id
     * @return the precondition
     */
	Precondition getPrecondition(String id);
	
	/**
     * add a precondition to the service
     *
     *@param precondition
     */
	void addPrecondition(Precondition precondition);
	
	/**
     * remove a precondition from the service by id
     *
     *@param id
     */
	void removePrecondition(String id);
	
	/**
     * get the effect list of the service
     *
     * @return the list of effects
     */
	HashMap<String, Effect> getEffects();
	
	/**
     * get an effect of the service by id
     *
     * @param id
     * @return the effect
     */
	Effect getEffect(String id);
	
	/**
     * add an effect to the service
     *
     *@param effect
     */
	void addEffect(Effect effect);
	
	/**
     * remove an effect from the service by id
     *
     *@param id
     */
	void removeEffect(String id);
}
