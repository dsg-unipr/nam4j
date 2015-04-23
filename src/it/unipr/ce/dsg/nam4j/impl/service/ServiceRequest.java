package it.unipr.ce.dsg.nam4j.impl.service;

import java.util.HashMap;

import it.unipr.ce.dsg.nam4j.interfaces.IServiceRequest;

public class ServiceRequest implements IServiceRequest {

	String id = "service";
	String name = "Service";
	String requestorName = "Name";
	String requestorAddress = "IP:PORT";
	
	HashMap<String,Parameter> inputs = new HashMap<String,Parameter>();
	HashMap<String,Parameter> outputs = new HashMap<String,Parameter>();
	HashMap<String,Effect> effects = new HashMap<String,Effect>();
	HashMap<String,Precondition> preconditions = new HashMap<String,Precondition>();
	
	/**
     * constructor
     */
	public ServiceRequest() {
	}
	
	/**
     * get the service name
     *
     * @return the name of the service
     */
	protected void setName(String name) {
		this.name = name;
	}

	/**
     * set the service id
     *
     * @param id
     */
	public String getName() {
		return name;
	}

	/**
     * get the service id
     *
     * @return the id of the service
     */
	public void setId(String id) {
		this.id = id;
	}

	/**
     * get the service id
     *
     * @return the id of the service
     */
	public String getId() {
		return id;
	}

	/**
     * get the input list of the service
     *
     * @return the list of inputs
     */
	public HashMap<String,Parameter> getInputs() {
		return inputs;
	}

	/**
     * get the input of the service by id
     *
     * @param id
     * @return the input
     */
	public Parameter getInput(String id) {
		return inputs.get(inputs.get(id));
	}

	/**
     * add an input to the service
     *
     *@param input
     */
	public void addInput(Parameter input) {
		inputs.put(input.getId(),input);
	}

	/**
     * remove input from service by id
     *
     *@param id
     */
	public void removeInput(String id) {
		inputs.remove(id);
	}

	/**
     * get the output list of the service
     *
     * @return the list of outputs
     */
	public HashMap<String,Parameter> getOutputs() {
		return outputs;
	}

	/**
     * get the output of the service by id
     *
     * @param id
     * @return the output
     */
	public Parameter getOutput(String id) {
		return outputs.get(id);
	}

	/**
     * add output to service
     *
     *@param output
     */
	public void addOutput(Parameter output) {
		outputs.put(output.getId(),output);
	}

	/**
     * remove output from service by id
     *
     *@param id
     */
	public void removeOutput(String id) {
		outputs.remove(id);
	}

	/**
     * get the precondition list of the service
     *
     * @return the list of preconditions
     */
	public HashMap<String,Precondition> getPreconditions() {
		return preconditions;
	}

	/**
     * get a precondition of the service by id
     *
     * @param id
     * @return the precondition
     */
	public Precondition getPrecondition(String id) {
		return preconditions.get(id);
	}

	/**
     * add a precondition to the service
     *
     *@param precondition
     */
	public void addPrecondition(Precondition precondition) {
		preconditions.put(precondition.getId(),precondition);
	}

	/**
     * remove a precondition from the service by id
     *
     *@param id
     */
	public void removePrecondition(String id) {
		preconditions.remove(id);
	}

	/**
     * get the effect list of the service
     *
     * @return the list of effects
     */
	public HashMap<String,Effect> getEffects() {
		return effects;
	}

	/**
     * get an effect of the service by id
     *
     * @param id
     * @return the effect
     */
	public Effect getEffect(String id) {
		return effects.get(id);
	}

	/**
     * add an effect to the service
     *
     *@param effect
     */
	public void addEffect(Effect effect) {
		effects.put(effect.getId(),effect);
	}

	/**
     * remove an effect from the service by id
     *
     *@param id
     */
	public void removeEffect(String id) {
		effects.remove(id);
	}

	/**
	 * 
	 * @return name of the NAM that requests the service
	 */
	public String getRequestorName() {
		return this.requestorName;
	}

	/**
	 * Set the name of the NAM that requests the service
	 * @param requestorName
	 */
	public void setRequestorName(String requestorName) {
		this.requestorName = requestorName;
	}

	/**
	 * 
	 * @return address (IP:PORT) of the NAM that requests the service
	 */
	public String getRequestorAddress() {
		return requestorAddress;
	}

	/**
	 * Set the address (IP:PORT) of the NAM that requests the service
	 * @param requestorAddress
	 */
	public void setRequestorAddress(String requestorAddress) {
		this.requestorAddress = requestorAddress;
	}

}
