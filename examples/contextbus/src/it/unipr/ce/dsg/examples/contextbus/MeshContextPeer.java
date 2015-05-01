package it.unipr.ce.dsg.examples.contextbus;

import it.unipr.ce.dsg.nam4j.impl.context.ContextBus;
import it.unipr.ce.dsg.nam4j.impl.context.ContextEvent;
import it.unipr.ce.dsg.nam4j.impl.messages.ContextEventActionRequestMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.ContextEventNotificationMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PeerListMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PingMessage;
import it.unipr.ce.dsg.nam4j.impl.mobility.utils.MobilityUtils;
import it.unipr.ce.dsg.nam4j.impl.peer.NamPeer;
import it.unipr.ce.dsg.nam4j.impl.service.Parameter;
import it.unipr.ce.dsg.nam4j.interfaces.IContextEventObserver;
import it.unipr.ce.dsg.nam4j.interfaces.IContextEventSubject;
import it.unipr.ce.dsg.s2p.centralized.message.PongMessage;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;
import it.unipr.ce.dsg.s2p.util.FileHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.zoolu.tools.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * <p>
 * This class represents a peer for the distributed context bus. Such a peer
 * supports a full mesh or a random graph network structure. In the 'Observer'
 * Design Pattern, {@link ContextBus} is the 'Observer', {@link MeshContextBus}
 * is the 'Concrete Observer', this class is the 'Concrete Subject'.
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

public class MeshContextPeer extends NamPeer implements IContextEventSubject {
	
	private FileHandler fileHandler;
	private Log log;
	
	private ArrayList<IContextEventObserver> observers = new ArrayList<IContextEventObserver>();
	private boolean joined = false;
	
	private String contextEventName;
	private String contextEventLocation;
	private String contextEventValue;
	
	public MeshContextPeer(String pathConfig, String key, String name, int port) {
		super(pathConfig, key, name, port);
		init();
	}
	
	/** Method to perform the initial setup of the peer. */
	private void init() {
		
		fileHandler = new FileHandler();
		
		if (nodeConfig.log_path != null){
			
			if (!fileHandler.isDirectoryExists(nodeConfig.log_path))
				fileHandler.createDirectory(nodeConfig.log_path);

			log = new Log(nodeConfig.log_path + "info_" + peerDescriptor.getAddress() + ".log", Log.LEVEL_MEDIUM);
		}
	}
	
	public String getContextEventName() {
		return contextEventName;
	}

	public void setContextEventName(String contextEventName) {
		this.contextEventName = contextEventName;
	}

	public String getContextEventLocation() {
		return contextEventLocation;
	}

	public void setContextEventLocation(String contextEventLocation) {
		this.contextEventLocation = contextEventLocation;
	}

	public String getContextEventValue() {
		return contextEventValue;
	}

	public void setContextEventValue(String contextEventValue) {
		this.contextEventValue = contextEventValue;
	}
	
	/**
	 * Method to send a message to another peer.
	 * 
	 * @param address
	 *            The address of the peer the message has to be sent to
	 * 
	 * @param message
	 *            The message to be sent
	 * 
	 * @param format
	 *            The format used to represent the message
	 */
	public void sendMessageToPeer(Address address, String message, String format) {
		sendMessage(address, address, this.getAddress(), message, format);
	}
	
	/**
	 * Method to send a context event to a list of interested peers.
	 * 
	 * @param contextEvent
	 *            The context event to be published
	 * 
	 * @param list
	 *            The list of peers interested in the event
	 */
	@Override
	public void notifyObservers(ContextEvent contextEvent, ArrayList<String> list) {
		
		ContextEventNotificationMessage peerMsg = new ContextEventNotificationMessage(null, Utils.PUBLISH_REQUEST, Utils.PUBLISH_REQUEST, contextEvent);
		
		for(String interestedPeer : list) {
			sendMessageToPeer(new Address(interestedPeer), peerMsg.getJSONString(), MobilityUtils.JSON_MESSAGE_FORMAT);
		}		
	}
	
	/**
	 * Method to store the reference of an observing peer.
	 * 
	 * @param observer
	 *            The reference to the peer to be stored
	 */
	@Override
	public void registerObserver(IContextEventObserver observer) {
		observers.add(observer);
	}

	/**
	 * Method to remove the reference of an observing peer.
	 * 
	 * @param observer
	 *            The reference to the peer to be removed
	 */
	@Override
	public void unregisterObserver(IContextEventObserver observer) {
		observers.remove(observer);
	}
	
	/**
	 * Method to manage received JSON messages represented as {@link JsonObject}
	 * objects.
	 * 
	 * @param peerMsg
	 *            The received message
	 * 
	 * @param sender
	 *            The {@link Address} of the sender
	 */
	@Override
	protected void onReceivedJSONMsg(JsonObject peerMsg, Address sender) {
		
		Gson gson = new Gson();
		
		String messageType = peerMsg.get("type").getAsString();
		
		// Logging
		if (nodeConfig.log_path != null) {

			int lengthMsg = messageType.length();

			JsonObject info = new JsonObject();
			info.addProperty("timestamp", System.currentTimeMillis());
			info.addProperty("type", "recv");
			info.addProperty("typeMessage", messageType);
			info.addProperty("byte", lengthMsg);
			info.addProperty("sender", sender.getURL());

			printJSONLog(info, log, false);
		}
		
		if (messageType.equals(PingMessage.MSG_KEY) || messageType.equals(PongMessage.MSG_KEY)) {
			
			// If a PING or a PONG message is received, the node adds the sender
			// to the list of known peers (if not yet included)
			
			PeerDescriptor senderPeerDescriptor = gson.fromJson(peerMsg.get("peer").toString(), PeerDescriptor.class);
			
			if ((!(this.getPeerList().contains(senderPeerDescriptor)))
					&& (!senderPeerDescriptor.getName().equals("bootstrap"))) {

				System.out.println("--- Adding a new peer to the peers list: "
						+ senderPeerDescriptor.getContactAddress());

				this.getPeerList().add(senderPeerDescriptor);
				
				this.getPeerList().printPeerList();
			}
			
			if (messageType.equals(PingMessage.MSG_KEY)) {
				
				System.out.println("Received PING message from " + senderPeerDescriptor.getContactAddress() + "\nSending pong message to " + senderPeerDescriptor.getContactAddress());
				
				// If the message was a ping, the node answers with a pong
				pong(senderPeerDescriptor.getContactAddress());
			} else {
				System.out.println("Received PONG message from " + senderPeerDescriptor.getContactAddress());
			}
			
		} else if (messageType.equals(PeerListMessage.MSG_KEY)) {
			
			// A peer list message is sent as the answer to a join request, and
			// whenever a peer asks for it. In the first case, a thread
			// monitoring the number of known peers gets started.
			
			if (!joined) {
				
				System.out.println("Joined the network");
				
				joined = true;
				
				// Start the thread checking if the peer list has enough elements
				CheckNumberOfKnownPeersRunnable checkNumberOfKnownPeersRunnable = new CheckNumberOfKnownPeersRunnable(this);
				Thread checkNumberOfKnownPeers = new Thread(checkNumberOfKnownPeersRunnable);
				checkNumberOfKnownPeers.start();
				
			} else {
				System.out.println("Peer list message received");
			}
						
			Type type = new TypeToken<Collection<PeerDescriptor>>(){}.getType();
			Collection<PeerDescriptor> peerSet = gson.fromJson(peerMsg.get("peers").toString(), type);
			
			// Adding the peers received from the bootstrap to the list of known peers
			// First, the already included peers get removed from the received list
			for(PeerDescriptor pd : this.getPeerList()) {
				for(PeerDescriptor receivedPd : peerSet) {
					if (pd.getContactAddress().equalsIgnoreCase(receivedPd.getContactAddress())) {
						peerSet.remove(receivedPd);
						break;
					}
				}
			}
			
			this.getPeerList().addAll(peerSet);
			this.getPeerList().printPeerList();
			
		} else if (messageType.equals(ContextEventActionRequestMessage.MSG_KEY)) {
			
			// Received a request message (subscribe or unsubscribe)
			
			if (peerMsg.get("infoType") != null) {
				System.out.println(peerMsg.get("infoType").getAsString() + " request received");
			}
			
			// Notify all observers that a request has been received
			for (IContextEventObserver observer : observers) {
				observer.manageRequest(peerMsg, this, this.getPeerList());
			}
			
		} else if (messageType.equals(ContextEventNotificationMessage.MSG_KEY)) {
			
			ContextEvent event = gson.fromJson(peerMsg.get("event").toString(), ContextEvent.class);
			
		    System.out.println("\n************************************************************");
		    System.out.println("Update received");
		    
		    Parameter location = event.getLocation();
		    Parameter subject = event.getSubject();
		    
			System.out.println(event.getSubject().getName()
					+ " value in location " + location.getValue() + " is "
					+ subject.getValue());
		    
		    System.out.println("************************************************************\n");
		}
	}
	
	/**
	 * Method invoked when a JSON message could not get delivered to the
	 * addressee.
	 * 
	 * @param peerMsg
	 *            The message that could not get delivered
	 * 
	 * @param receiver
	 *            The {@link Address} of the addressee peer
	 * 
	 * @param contentType
	 *            The format of the message (i.e. "application/json")
	 */
	protected void onDeliveryMsgFailure(String sentMessage, Address receiver, String contentType) {
		System.out.println("Could not deliver message to " + receiver);
		
		for(PeerDescriptor pd : this.getPeerList()) {
			if (pd.getContactAddress().equalsIgnoreCase(receiver.getURL())) {
				
				System.out.println("Removing the peer from the peer list.");
				
				this.getPeerList().remove(pd);
				this.getPeerList().printPeerList();
				
				break;
			}
		}
	}

	/**
	 * Method invoked when a JSON message got successfully delivered to the
	 * addressee.
	 * 
	 * @param peerMsg
	 *            The message that got delivered
	 * 
	 * @param receiver
	 *            The {@link Address} of the addressee peer
	 * 
	 * @param contentType
	 *            The format of the message (i.e. "application/json")
	 */
	protected void onDeliveryMsgSuccess(String sentMessage, Address receiver, String contentType) {}
	
	/**
	 * Thread to monitor the number of known peers so that it does not fall
	 * behind a specified threshold. If it does, a list of peers is requested to
	 * the bootstrap.
	 */
	class CheckNumberOfKnownPeersRunnable implements Runnable {

		boolean stopThread = false;

		private NamPeer peer;

		public CheckNumberOfKnownPeersRunnable(NamPeer peer) {
			this.peer = peer;
		}

		@Override
		public void run() {

			while (!stopThread) {
				
				if (peer.getPeerList().size() < Utils.MINIMUM_NUMBER_OF_PEERS) {
					if (peer.getPeerConfig().getBootstrapContactAddress() != null) {
						
						System.out.println("--- The peer list size is below the threshold. Asking for new peers...");
						
						peer.requestPeers(peer.getPeerConfig().getBootstrapContactAddress());
					}
				}
				
				try {
					Thread.sleep(Utils.THREAD_SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Method to stop the execution of the thread.
		 */
		public void stopThread() {
			stopThread = true;
		}
	}

}
