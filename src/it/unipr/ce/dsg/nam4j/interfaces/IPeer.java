package it.unipr.ce.dsg.nam4j.interfaces;

import it.unipr.ce.dsg.nam4j.impl.peer.PeerConfig;
import it.unipr.ce.dsg.nam4j.impl.peer.PeerList;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

/**
 * <p>
 * This interface represents a peer.
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

public interface IPeer {

	/**
	 * Method to get the {@link PeerDescriptor} object describing the peer.
	 * 
	 * @return the {@link PeerDescriptor} object describing the peer
	 */
	PeerDescriptor getPeerDescriptor();

	/**
	 * Method to get the {@link PeerConfig} object that contains the
	 * configuration of the peer.
	 * 
	 * @return the {@link PeerConfig} object that contains the configuration of
	 *         the peer
	 */
	PeerConfig getPeerConfig();

	/**
	 * Method to join the network.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the peer to be contacted to join the network
	 */
	void join(String peerToBeContactedAddress);

	/**
	 * Method to leave the network.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the peer to be contacted to leave the network
	 */
	void leave(String peerToBeContactedAddress);

	/**
	 * Method to get the list of known peers.
	 * 
	 * @return the list of known peers
	 */
	PeerList getPeerList();

	/**
	 * Method to request a list of peers to a node.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	void requestPeers(String peerToBeContactedAddress);

	/**
	 * Method to ping a peer.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	public void ping(String peerToBeContactedAddress);

	/**
	 * Method to answer a ping message.
	 * 
	 * @param peerToBeContactedAddress
	 *            The address of the node to which the request has to be sent
	 */
	void pong(String peerToBeContactedAddress);

}
