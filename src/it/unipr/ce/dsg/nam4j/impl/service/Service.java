package it.unipr.ce.dsg.nam4j.impl.service;

import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.logger.NamLogger;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import java.io.Serializable;
import java.util.HashMap;

/**
 * <p>
 * This class represents a service.
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

public class Service implements IService {

	private String id = "service";
	private String name = "Service";
	private FunctionalModule functionalModule = null;
	
	HashMap<String,Parameter> inputs = new HashMap<String,Parameter>();
	HashMap<String,Parameter> outputs = new HashMap<String,Parameter>();
	HashMap<String,Effect> effects = new HashMap<String,Effect>();
	HashMap<String,Precondition> preconditions = new HashMap<String,Precondition>();
	
	/** The logger object */
	private NamLogger messageLogger = new NamLogger("Service");
	
	/**
     * Default constructor.
     */
	public Service() {}
	
	/**
	 * Class constructor.
	 * 
	 * @param functionalModule
	 *            a reference to the {@link FunctionalModule} to which the
	 *            Service is bound
	 */
	public Service(FunctionalModule functionalModule) {
		this.functionalModule = functionalModule;
	}
	
	/**
	 * Method to get the {@link FunctionalModule} to which the Service is
	 * associated.
	 * 
	 * @return the {@link FunctionalModule} to which the Service is associated
	 */
	public FunctionalModule getFunctionalModule() {
		return this.functionalModule;
	}

	/**
	 * Method to set the {@link FunctionalModule} to which the Service is
	 * associated.
	 * 
	 * @param functionalModule
	 *            the {@link FunctionalModule} to which the Service is
	 *            associated
	 */
	public void setFunctionalModule(FunctionalModule functionalModule) {
		this.functionalModule = functionalModule;
	}
	
	/**
     * Method to get the service name.
     *
     * @return the name of the service
     */
	protected void setName(String name) {
		this.name = name;
	}

	/**
     * Method to set the service id.
     *
     * @param id
     */
	public String getName() {
		return name;
	}

	/**
     * Method to get the service id.
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
	 * Method to get the input list of the service.
	 *
	 * @return the list of inputs
	 */
	public HashMap<String,Parameter> getInputs() {
		return inputs;
	}

	/**
	 * Method to get the input of the service by id.
	 *
	 * @param id
	 *            The id of the service
	 * 
	 * @return the input of the service
	 */
	public Parameter getInput(String id) {
		return inputs.get(inputs.get(id));
	}

	/**
	 * Method to add an input to the service.
	 *
	 * @param input
	 *            The input to add to the service
	 */
	public void addInput(Parameter input) {
		inputs.put(input.getId(),input);
	}

	/**
	 * Method to remove an input from service by id.
	 *
	 * @param id
	 *            The id of the input to be removed
	 */
	public void removeInput(String id) {
		inputs.remove(id);
	}

	/**
	 * Method to get the list of outputs of the service.
	 *
	 * @return the list of outputs
	 */
	public HashMap<String,Parameter> getOutputs() {
		return outputs;
	}

	/**
	 * Method to get the output of the service by id.
	 *
	 * @param id
	 *            The id of the output
	 * 
	 * @return the output
	 */
	public Parameter getOutput(String id) {
		return outputs.get(id);
	}

	/**
	 * Method to add an output to the service.
	 *
	 * @param output
	 *            The output to be added
	 */
	public void addOutput(Parameter output) {
		outputs.put(output.getId(),output);
	}

	/**
	 * Method to remove the output from a service by id.
	 *
	 * @param id
	 *            The id of the output to be removed
	 */
	public void removeOutput(String id) {
		outputs.remove(id);
	}

	/**
	 * Method get the list of preconditions of the service.
	 *
	 * @return the list of preconditions
	 */
	public HashMap<String,Precondition> getPreconditions() {
		return preconditions;
	}

	/**
	 * Method to get a precondition of the service by id.
	 *
	 * @param id
	 *            The id of the precondition
	 * 
	 * @return the precondition
	 */
	public Precondition getPrecondition(String id) {
		return preconditions.get(id);
	}

	/**
	 * Method to add a precondition to the service.
	 *
	 * @param precondition
	 *            The precondition to be added
	 */
	public void addPrecondition(Precondition precondition) {
		preconditions.put(precondition.getId(),precondition);
	}

	/**
	 * Method to remove a precondition from the service by id.
	 *
	 * @param id
	 *            The id of the precondition to be removed
	 */
	public void removePrecondition(String id) {
		preconditions.remove(id);
	}

	/**
	 * Method to get the list of effects of the service.
	 *
	 * @return the list of effects
	 */
	public HashMap<String,Effect> getEffects() {
		return effects;
	}

	/**
	 * Method to get an effect of the service by id.
	 *
	 * @param id
	 *            The id of the effect
	 * 
	 * @return the effect
	 */
	public Effect getEffect(String id) {
		return effects.get(id);
	}

	/**
	 * Method to add an effect to the service.
	 *
	 * @param effect
	 *            The effect to be added
	 */
	public void addEffect(Effect effect) {
		effects.put(effect.getId(),effect);
	}

	/**
	 * Method to remove an effect from the service by id.
	 *
	 * @param id
	 *            The id of the effect to be removed
	 */
	public void removeEffect(String id) {
		effects.remove(id);
	}
	
	
	// XXX The following items are used for mobility actions
	
	/**
	 * The method has to be overridden so that it returns the
	 * {@link ServiceRunnable} object whose code is executed by the
	 * Service.
	 * 
	 * @return the {@link Runnable} object whose code is executed by the
	 *         Functional Module
	 */
	public ServiceRunnable getServiceRunnable() {
		return null;
	}
	
	/**
	 * The {@link Runnable} object whose code is executed by the Service and can
	 * be migrated.
	 */
	public abstract class ServiceRunnable implements Runnable, Serializable {
		private static final long serialVersionUID = -4617371070752993784L;
		
		// Java Threads cannot be serialized, so it is declared as transient
		transient private Thread thread;
		
		private boolean suspended = false;
		
		/** Start the execution of the Service main thread */
		public void start() {
			if (thread == null) {
				messageLogger.debug("Starting execution...");
				thread = new Thread(this);
				thread.start();
			}
		}
		
		/** Suspend the execution of the Service main thread */
		public void suspend() {
			messageLogger.debug("Suspending execution...");
			this.suspended = true;
		}

		/** Resume the execution of the Service main thread after suspension */
		public synchronized void resume() {
			messageLogger.debug("Resuming execution...");
			
			suspended = false;
			
			// If the Runnable has been migrated, a new thread has to be created
			if (thread == null) {
				thread = new Thread(this);
				thread.start();
			}
			// Else, resume the already existing thread
			else {		
				notify();
			}
		}
		
		/** Stop the execution of the Service main thread */
		@SuppressWarnings("deprecation")
		public void stop() {
			if (thread != null) {
				messageLogger.debug("Execution stopped");
				thread.stop();
			}
		}
		
		/**
		 * Save the state of the Service main thread. Such a method is
		 * intended to be overridden to save the state of non-serializable
		 * attributes (e.g. FileReader, BufferReader, FileInputStream,
		 * files...). The other attributes do not need to be managed by this
		 * method.
		 */
		public void saveState() {}
		
		/**
		 * Restore the state of the Service main thread. Such a method
		 * is intended to be overridden to restore the state of non-serializable
		 * attributes (e.g. FileReader, BufferReader, FileInputStream,
		 * files...). The other attributes do not need to be managed by this
		 * method.
		 */
		public void restoreState() {}
		
		public ServiceRunnable() {}

		public boolean isSuspended() {
			return suspended;
		}

		public void setSuspended(boolean suspended) {
			this.suspended = suspended;
		}
	}
	
	// XXX End of items used for migration
	
}
