package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.examples.contextbus.MeshContextPeer;
import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;
import it.unipr.ce.dsg.nam4j.impl.peer.PeerList;

import com.google.gson.JsonObject;

/**
 * <p>
 * This interface represents a contextual events observer.
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
public interface IContextEventObserver {
	
	/**
	 * Method used to publish an event on the distributed context bus.
	 * 
	 * @param contextEvent
	 *            The event to be published
	 * 
	 * @param peer
	 *            The context event publisher
	 */
	void publish(ContextEvent contextEvent, IPeer peer);
	
	/**
	 * Method used to manage requests received from the subjects of the Observer
	 * design pattern.
	 * 
	 * @param peerMsg
	 *            The message containing the request parameters
	 * 
	 * @param peer
	 *            The {@link MeshContextPeer} object of the requesting peer
	 * 
	 * @param list
	 *            A {@link PeerList} object containing the list of peers to
	 *            which the notification has to be forwarded
	 */
	void manageRequest(JsonObject peerMsg, MeshContextPeer peer, PeerList list);
}
