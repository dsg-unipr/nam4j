package it.unipr.ce.dsg.examples.contextbus;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine;
import it.unipr.ce.dsg.nam4j.impl.context.ContextBus;
import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;
import it.unipr.ce.dsg.nam4j.impl.logger.Logger;
import it.unipr.ce.dsg.nam4j.impl.messages.ContextEventActionRequestMessage;
import it.unipr.ce.dsg.nam4j.impl.peer.PeerList;
import it.unipr.ce.dsg.nam4j.interfaces.IService;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

import java.util.ArrayList;

import com.google.gson.JsonObject;

/**
 * <p>
 * This class represents an implementation of the distributed context bus (i.e.,
 * in the 'Observer' Design Pattern, {@link ContextBus} is the 'Observer', this
 * class is the 'Concrete Observer', {@link MeshContextPeer} is the 'Concrete
 * Subject'). Such an implementation supports a full mesh or a random graph
 * network structure.
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

public class MeshContextBus extends ContextBus {

	private Logger logger = null;
	private ArrayList<String> eventModuleList = null;
	
	public MeshContextBus(NetworkedAutonomicMachine nam, String id) {
		super(nam);
		this.logger = new Logger("log/", "ContextBusLogs" + id + ".txt");
		this.eventModuleList = new ArrayList<String>();
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Add a functional module subscription for a certain type of context event
	 * and forward the request to the known peers. This method is invoked when a
	 * random graph structure is in use.
	 * 
	 * @param contextEventName
	 *            The name of the context event for which the unsubscribe is
	 *            requested
	 * 
	 * @param functionalModuleId
	 *            The id of the functional module
	 * 
	 * @param sender
	 *            The sender of the request
	 * 
	 * @param peer
	 *            The {@link MeshContextPeer} object of the requesting peer
	 * 
	 * @param list
	 *            The peer list of the requesting node
	 * 
	 * @param hops
	 *            The number of remaining hops for the request (time to live);
	 *            if full mesh structure is in use, the value must be negative
	 */
	public void subscribe(String contextEventName, String functionalModuleId, String sender, MeshContextPeer peer, PeerList list, int hops) {
		
		if (hops < 0) {
			
			// Full mesh structure in use
			
			String eventModule = contextEventName + ":" + functionalModuleId + "/" + peer.getPeerDescriptor().getContactAddress();

			eventModuleList.add(eventModule);
			logger.log("Added " + eventModule + " to eventModuleList");
			
			for(PeerDescriptor pd : list) {
				if (!pd.getContactAddress().equals(peer.getPeerDescriptor().getContactAddress())){
					
					ContextEventActionRequestMessage contextMsg = new ContextEventActionRequestMessage(null, eventModule, Utils.SUBSCRIBE_REQUEST, -1, null);
					
					peer.sendMessageToPeer(new Address(pd.getContactAddress()), contextMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
				}
			}
			
		} else {
			
			// Random graph structure in use
			
			ContextEventActionRequestMessage contextMsg;
			
			for(PeerDescriptor s : list) {			
				if (!s.getContactAddress().equals(peer.getPeerDescriptor().getContactAddress())){
					
					if (hops == Utils.HOPS_NUMBER) {
						
						String eventModule = contextEventName + ":" + functionalModuleId;
						
						if (!eventModuleList.contains(eventModule + "/" + peer.getPeerDescriptor().getContactAddress())) {
							eventModuleList.add(eventModule + "/" + peer.getPeerDescriptor().getContactAddress());
							logger.log("added " + eventModule + " from " + peer.getPeerDescriptor().getContactAddress() + " to eventModuleList");
						}	
						
						contextMsg = new ContextEventActionRequestMessage(null, eventModule, Utils.SUBSCRIBE_REQUEST, hops, peer.getPeerDescriptor().getContactAddress().toString());
					}
					else{						
						contextMsg = new ContextEventActionRequestMessage(null, contextEventName, Utils.SUBSCRIBE_REQUEST, hops, sender);
					}

					peer.sendMessageToPeer(new Address(s.getContactAddress()), contextMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
				}			
			}
		}
	}
	
	/**
	 * Remove a functional module subscription for a certain type of context
	 * event and forwarding the request to the list of NAM "hops" times. This
	 * method is invoked when a random graph structure is in use.
	 * 
	 * @param contextEventName
	 *            The name of the context event for which the unsubscribe is
	 *            requested
	 * 
	 * @param functionalModuleId
	 *            The id of the functional module
	 * 
	 * @param sender
	 *            The sender of the request
	 * 
	 * @param peer
	 *            The {@link MeshContextPeer} object of the requesting peer
	 * 
	 * @param list
	 *            The peer list of the requesting node
	 * 
	 * @param hops
	 *            The number of remaining hops for the request (time to live);
	 *            if full mesh structure is in use, the value must be negative
	 */
	public void unsubscribe(String contextEventName, String functionalModuleId, String sender, MeshContextPeer peer, PeerList list, int hops) {

		if (hops < 0) {
			
			// Full Mesh structure
			
			String eventModule = contextEventName + ":" + functionalModuleId + "/" + peer.getPeerDescriptor().getContactAddress();
			
			eventModuleList.remove(eventModule);
			logger.log("removed " + eventModule + " from eventModuleList");
			
			for(PeerDescriptor pd : list) {
				
				if (!pd.getContactAddress().equals(peer.getPeerDescriptor().getContactAddress())){
					
					ContextEventActionRequestMessage contextMsg = new ContextEventActionRequestMessage(null, eventModule, Utils.UNSUBSCRIBE_REQUEST, -1, null);
					
					peer.sendMessageToPeer(new Address(pd.getContactAddress()), contextMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
				}
			}
			
		} else {
			
			// Random Graph structure
			
			ContextEventActionRequestMessage contextMsg;
					
			for(PeerDescriptor s : list){			
				if (!s.getContactAddress().equals(peer.getPeerDescriptor().getContactAddress())) {
								
					if (hops == Utils.HOPS_NUMBER){
						
						String eventModule = contextEventName + ":" + functionalModuleId;
						
						if (eventModuleList.contains(eventModule + "/" + peer.getPeerDescriptor().getContactAddress())) {
							eventModuleList.remove(eventModule + "/" + peer.getPeerDescriptor().getContactAddress());
							logger.log("Removed " + eventModule + " from " + peer.getPeerDescriptor().getContactAddress() + " to eventModuleList");			
						}
	
						contextMsg = new ContextEventActionRequestMessage(null, eventModule, Utils.UNSUBSCRIBE_REQUEST, hops, peer.getPeerDescriptor().getContactAddress().toString());
					}
					else {					
						contextMsg = new ContextEventActionRequestMessage(null, contextEventName, Utils.UNSUBSCRIBE_REQUEST, hops, sender);
					}
	
					peer.sendMessageToPeer(new Address(s.getContactAddress()), contextMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
				}
			}
		}
	}
	
	/**
	 * Publish a context event to the context bus; such a context event will be
	 * forwarded to all interested functional modules.
	 * 
	 * @param contextEvent
	 * @param peer
	 */
	public void publish(ContextEvent contextEvent, MeshContextPeer peer) {

		ArrayList<String> interestedNam = new ArrayList<String>();
		
		System.out.println("Generated event: " + peer.getContextEventName());
		
		for (String s : eventModuleList) {
			if (s.startsWith(peer.getContextEventName())) {
				if (!s.substring(s.indexOf("/") + 1).equals(peer.getPeerDescriptor().getContactAddress())) {
					interestedNam.add(s.substring(s.indexOf("/") + 1));
				}
			}
		}
		peer.notifyObservers(contextEvent, interestedNam);
	}

	@Override
	public void addConsumableService(String id, IService service) {}

	@Override
	public void addProvidedService(String id, IService service) {}
	
	public ArrayList<String> getEventList() {
		return eventModuleList;
	}

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
	@Override
	public void manageRequest(JsonObject peerMsg, MeshContextPeer peer, PeerList list) {
		
		String name = peerMsg.get("info").getAsString();
		String type = peerMsg.get("infoType").getAsString();
		
		if (peerMsg.get("sender") == null) {
			
			// Full mesh structure is in use
			
			String receivedName = name.substring(0, name.indexOf(":"));
			
			if (type.equals(Utils.SUBSCRIBE_REQUEST)){
				
				if (!eventModuleList.contains(name)){
					
					eventModuleList.add(name);
					logger.log("Added " + name + " to eventModuleList");
				}					
			}
			else if (type.equals(Utils.UNSUBSCRIBE_REQUEST)) {
				
				if (eventModuleList.contains(name)) {
					
					eventModuleList.remove(name);
					logger.log("Removed " + name + " from eventModuleList");
				}		
			}
					
			System.out.println("--- Received a " + type + " request for context event: '" + receivedName + "'");
			
		} else {
			
			// Random graph structure is in use
		
			String sender = peerMsg.get("sender").getAsString();
			int hops = peerMsg.get("hops").getAsInt();
			
			String infoReceived = name + "/" + sender;
	
			if (type.equals(Utils.SUBSCRIBE_REQUEST)) {
								
				if (!eventModuleList.contains(infoReceived)) {
					eventModuleList.add(infoReceived);
					
					System.out.println("--- Added subscription request for event " + name + ", from peer " + sender);
					
					logger.log("added " + name + " from " + sender + " to eventModuleList");			
				} else {
					System.out.println("--- The subscription has already been recorded");
				}
				
				if (hops > 0) {
					
					// The message has to be forwarded hops more times
					
					System.out.println("\n************************************************************");
					System.out.println("Received a " + Utils.SUBSCRIBE_REQUEST + " message for event " + name.substring(0, name.indexOf(":")));
					System.out.println("Hops to go: " + hops);
					
					hops--;
					this.subscribe(name, "", sender, peer, list, hops);
					
					System.out.println("The request has been forwarded (" + hops + " hops to go)");
					System.out.println("************************************************************\n");
				}			
			}
			else if (type.equals(Utils.UNSUBSCRIBE_REQUEST)) {
								
				if (eventModuleList.contains(infoReceived)){
					eventModuleList.remove(infoReceived);
					
					System.out.println("--- Removed subscription request for event " + name + ", from peer " + sender);

					logger.log("remove " + name + " from " + sender + " to eventModuleList");			
				} else {
					System.out.println("--- The specified subscription is not recorded");
				}
				
				if (hops > 0) {
					
					// The message has to be forwarded hops more times
					
					System.out.println("\n************************************************************");
					System.out.println("Received a " + Utils.UNSUBSCRIBE_REQUEST + " message for event " + name.substring(0, name.indexOf(":")));
					System.out.println("Hops to go: " + hops);
					
					hops--;				
					this.unsubscribe(name, "", sender, peer, list, hops);
					
					System.out.println("The request has been forwarded (" + hops + " hops to go)");
					System.out.println("************************************************************\n");
				}
			}
		}
	}
}
