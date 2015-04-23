package it.unipr.ce.dsg.nam4j.impl.context;

import it.unipr.ce.dsg.examples.contextbus.MeshContextPeer;
import it.unipr.ce.dsg.nam4j.impl.FunctionalModule;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.peer.PeerList;
import it.unipr.ce.dsg.nam4j.interfaces.IContextEventObserver;
import it.unipr.ce.dsg.nam4j.interfaces.IPeer;
import it.unipr.ce.dsg.nam4j.interfaces.IService;

import com.google.gson.JsonObject;

/**
 * <p>
 * This class represents the distributed context bus.
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
 * @author Nicholas Barbieri
 * @author Luca Barili
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public abstract class ContextBus extends FunctionalModule implements IContextEventObserver {

	public ContextBus(NetworkedAutonomicMachine nam) {
		super(nam);
	}

	@Override
	public void addConsumableService(String id, IService service) {}

	@Override
	public void addProvidedService(String id, IService service) {}

	@Override
	public void publish(ContextEvent contextEvent, IPeer peer) {}
	
	@Override
	public void manageRequest(JsonObject peerMsg,
			MeshContextPeer peer, PeerList list) {}

}
