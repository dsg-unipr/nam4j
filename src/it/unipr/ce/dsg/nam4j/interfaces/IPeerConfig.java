package it.unipr.ce.dsg.nam4j.interfaces;

/**
 * <p>
 * This interface represents the configuration of a peer.
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

public interface IPeerConfig {
	
	/**
	 * Method to get the bootstrap's contact address.
	 * 
	 * @return the bootstrap's contact address
	 */
	String getBootstrapContactAddress();
	
	/**
	 * Method to set the bootstrap's contact address.
	 * 
	 * @param bootstrap
	 *            the bootstrap's contact address
	 */
	void setBootstrapContactAddress(String bootstrap);
	
	/**
	 * Method to get the peer's key.
	 * 
	 * @return the key of the peer
	 */
	String getKey();
	
	/**
	 * Method to set the peer's key.
	 * 
	 * @param key
	 *            the peer's key
	 */
	void setKey(String key);
	
	/**
	 * Method to get the peer's name.
	 * 
	 * @return the peer's name
	 */
	String getName();
	
	/**
	 * Method to set the peer's name.
	 * 
	 * @param name
	 *            the peer's name
	 */
	void setName(String name);

	/**
	 * Method to get the peer's port.
	 * 
	 * @return the peer's port
	 */
	int getPort();

	/**
	 * Method to set the peer's port.
	 * 
	 * @param port
	 *            the peer's port
	 */
	void setPort(int port);

}
