package it.unipr.ce.dsg.nam4j.impl.peer;

import it.unipr.ce.dsg.examples.contextbus.Utils;
import it.unipr.ce.dsg.nam4j.impl.messages.JoinRequestMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.LeaveRequestMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PeerListRequestMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PingMessage;
import it.unipr.ce.dsg.nam4j.impl.messages.PongMessage;
import it.unipr.ce.dsg.nam4j.interfaces.IPeer;
import it.unipr.ce.dsg.s2p.peer.Peer;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;
import it.unipr.ce.dsg.s2p.sip.Address;

import com.google.gson.JsonObject;

/**
 * <p>
 * This class represents a peer.
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
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public abstract class NamPeer extends Peer implements IPeer {

	/** The configuration of the peer. */
	private PeerConfig peerConfig;
	
	/** The list of peers known by the node. */
	private PeerList peers;
	
	/**
	 * Constructor allowing to set the path to the configuration file, the key,
	 * name and port of the peer.
	 * 
	 * @param configFilePath
	 *            The path to the configuration file
	 * 
	 * @param key
	 *            The key of the peer
	 * 
	 * @param name
	 *            The name of the peer
	 * 
	 * @param port
	 *            The port on which the peer is listening
	 */
	public NamPeer(String configFilePath, String key, String name, int port) {
		super(configFilePath, key, name, port);
		this.peerConfig = new PeerConfig(configFilePath, key, name, port);
		init();
	}
	
	/**
	 * Constructor allowing to set the path to the configuration file and the
	 * key of the peer.
	 * 
	 * @param pathConfig
	 *            The path to the configuration file
	 * 
	 * @param key
	 *            The key of the peer
	 */
	public NamPeer(String pathConfig, String key) {
		super(pathConfig, key);
		init();
	}
	
	/**
	 * Method to perform the initial setup of the peer.
	 */
	private void init() {
		this.peers = new PeerList();
	}
	
	/**
	 * Method to get the {@link PeerDescriptor} object describing the peer.
	 * 
	 * @return the {@link PeerDescriptor} object describing the peer
	 */
	@Override
	public PeerDescriptor getPeerDescriptor() {
		return peerDescriptor;
	}

	/**
	 * Method to get the {@link PeerConfig} object that contains the
	 * configuration of the peer.
	 * 
	 * @return the {@link PeerConfig} object that contains the configuration of
	 *         the peer
	 */
	@Override
	public PeerConfig getPeerConfig() {
		return peerConfig;
	}

	/**
	 * Method to join the network.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the peer to be contacted to join the network
	 */
	@Override
	public void join(String peerToBeContactedAddress) {
		JoinRequestMessage peerMsg = new JoinRequestMessage(peerDescriptor);		
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
	}

	/**
	 * Method to leave the network.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the peer to be contacted to leave the network
	 */
	@Override
	public void leave(String peerToBeContactedAddress) {
		LeaveRequestMessage peerMsg = new LeaveRequestMessage(peerDescriptor);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
	}

	/**
	 * Method to get the list of known peers.
	 * 
	 * @return the list of known peers
	 */
	@Override
	public PeerList getPeerList() {
		return peers;
	}
	
	/**
	 * Method to request a list of peers to a node.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	@Override
	public void requestPeers(String peerToBeContactedAddress) {
		PeerListRequestMessage peerMsg = new PeerListRequestMessage(peerDescriptor);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
	}
	
	/**
	 * Method to ping a peer.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	@Override
	public void ping(String peerToBeContactedAddress) {
		PingMessage peerMsg = new PingMessage(peerDescriptor);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
	}
	
	/**
	 * Method to answer a ping message.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	@Override
	public void pong(String peerToBeContactedAddress) {
		PongMessage peerMsg = new PongMessage(peerDescriptor);
		sendMessage(new Address(peerToBeContactedAddress), new Address(peerToBeContactedAddress), this.getAddress(), peerMsg.getJSONString(), Utils.JSON_MESSAGE_FORMAT);
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
	protected void onReceivedJSONMsg(JsonObject peerMsg, Address sender) {}
	
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
	@Override
	protected void onDeliveryMsgFailure(String sentMessage, Address receiver,
			String contentType) {}

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
	@Override
	protected void onDeliveryMsgSuccess(String sentMessage, Address receiver,
			String contentType) {}

}
