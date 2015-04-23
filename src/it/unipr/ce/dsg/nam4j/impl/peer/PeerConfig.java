package it.unipr.ce.dsg.nam4j.impl.peer;

import it.unipr.ce.dsg.nam4j.interfaces.IPeerConfig;

import org.zoolu.tools.Configure;
import org.zoolu.tools.Parser;

/**
 * <p>
 * This class represents the configuration of a peer.
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

public class PeerConfig extends Configure implements IPeerConfig {
	
	private String bootstrapContactAddress = null;
	private String key;
	private String name;
	private int port;
	
	/**
	 * Constructor.
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
	public PeerConfig(String configFilePath, String key, String name, int port) {
		loadFile(configFilePath);
		setKey(key);
		setName(name);
		setPort(port);
	}
	
	/**
	 * Method to get the bootstrap's contact address.
	 * 
	 * @return the bootstrap's contact address
	 */
	@Override
	public String getBootstrapContactAddress() {
		return bootstrapContactAddress;
	}
	
	/**
	 * Method to set the bootstrap's contact address.
	 * 
	 * @param bootstrap
	 *            the bootstrap's contact address
	 */
	@Override
	public void setBootstrapContactAddress(String bootstrap) {
		this.bootstrapContactAddress = bootstrap;
	}
	
	/**
	 * Method to get the peer's key.
	 * 
	 * @return the key of the peer
	 */
	@Override
	public String getKey() {
		return key;
	}
	
	/**
	 * Method to set the peer's key.
	 * 
	 * @param key
	 *            the peer's key
	 */
	@Override
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Method to get the peer's name.
	 * 
	 * @return the peer's name
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Method to set the peer's name.
	 * 
	 * @param name
	 *            the peer's name
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Method to get the peer's port.
	 * 
	 * @return the peer's port
	 */
	@Override
	public int getPort() {
		return port;
	}

	/**
	 * Method to set the peer's port.
	 * 
	 * @param port
	 *            the peer's port
	 */
	@Override
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Method to parse a line loaded from the configuration file.
	 * 
	 * @param line
	 *            the line to be parsed
	 */
	protected void parseLine(String line) {

		String attribute;
		Parser par;
		int index = line.indexOf("=");
		
		if (index > 0) {
			attribute = line.substring(0, index).trim();
			par = new Parser(line, index + 1);
		} else {
			attribute = line;
			par = new Parser("");
		}

		if (attribute.equals("bootstrap_peer")) {
			setBootstrapContactAddress(par.getString());
			return;
		}
	}

}
