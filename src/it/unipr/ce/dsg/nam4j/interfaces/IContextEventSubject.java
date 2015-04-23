package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;

import java.util.ArrayList;

/**
 * <p>
 * Interface of the subject for the Observer design pattern. The subject
 * maintains a list of its dependents, called observers, and notifies them
 * automatically of any state change.
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
 * @author Nicholas Barbieri
 * @author Luca Barili
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */
public interface IContextEventSubject {
	
	/**
	 * Method to store the reference of an observing peer.
	 * 
	 * @param observer
	 *            The reference to the peer to be stored
	 */
	public void registerObserver(IContextEventObserver observer);
	
	/**
	 * Method to remove the reference of an observing peer.
	 * 
	 * @param observer
	 *            The reference to the peer to be removed
	 */
	public void unregisterObserver(IContextEventObserver observer);
	
	/**
	 * Method to send a context event to a list of interested peers.
	 * 
	 * @param contextEvent
	 *            The context event to be published
	 * 
	 * @param list
	 *            The list of peers interested in the event
	 */
	public void notifyObservers(ContextEvent contextEvent, ArrayList<String> list);
}
