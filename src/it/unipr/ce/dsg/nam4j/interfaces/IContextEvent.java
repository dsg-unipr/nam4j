package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;

/**
 * <p>
 * This interface represents a context event.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public interface IContextEvent extends IStatement {

	/**
	 * Method to set the timestamp of the context event.
	 *
	 * @param timestamp
	 */
	void setTimestamp(String timestamp);

	/**
	 * Method to get the timestamp of the context event.
	 *
	 * @return the timestamp of the context event
	 */
	String getTimestamp();

	/**
	 * Method to set the temporal validity of the context event.
	 * 
	 * @param temporalValidity
	 */
	void setTemporalValidity(String temporalValidity);

	/**
	 * Method to get the the temporal validity of the context event.
	 *
	 * @return the temporal validity of the context event
	 */
	String getTemporalValidity();

	/**
	 * Method to validate the context event.
	 *
	 * @return the value indicating the validation of the context event
	 */
	Boolean validateContextEvent(ContextEvent contextEvent);

	/**
	 * Method to return the producer of the context event.
	 *
	 * @return the id of the context event producer
	 */
	String getContextEventProducer();

	/**
	 * Method to set the producer of the context event.
	 *
	 * @param the
	 *            id of the context event producer
	 */
	void setContextEventProducer(String producerId);
}
