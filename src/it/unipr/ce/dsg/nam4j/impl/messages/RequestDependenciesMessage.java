package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.Dependency;

import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents the list of dependencies requested to be sent along
 * with the requested item.
 * </p>
 * 
 * <p>
 * Copyright (c) 2014, Distributed Systems Group, University of Parma, Italy.
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

public class RequestDependenciesMessage {

	public static final String MSG_KEY = "REQUEST_DEPENDENCIES";
	
	private String conversationKey;
	
	// List of requested dependencies' id and version
	private ArrayList<Dependency> dependencies;
	
	private String type;
	private byte[] encodedPublicKey;

	public RequestDependenciesMessage(String conversationKey, byte[] encodedPublicKey) {
		setType(MSG_KEY);
		setConversationKey(conversationKey);
		dependencies = new ArrayList<Dependency>();
		setEncodedPublicKey(encodedPublicKey);
	}
	
	public String getConversationKey() {
		return conversationKey;
	}

	public void setConversationKey(String conversationKey) {
		this.conversationKey = conversationKey;
	}
	
	public void addItem(Dependency dependency) {
		boolean found = false;
		for (Dependency dependencyInList : dependencies) {
			if (dependencyInList.getId().equals(dependency.getId())) {
				found = true;
				break;
			}
		}
		
		if (!found)
			dependencies.add(dependency);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public byte[] getEncodedPublicKey() {
		return encodedPublicKey;
	}
	
	public void setEncodedPublicKey(byte[] encodedPublicKey) {
		this.encodedPublicKey = encodedPublicKey;
	}

	/**
	 * Method to obtain a JSON representation of this
	 * {@link RequestDependenciesMessage}.
	 * 
	 * @return a JSON representation of this {@link RequestDependenciesMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
