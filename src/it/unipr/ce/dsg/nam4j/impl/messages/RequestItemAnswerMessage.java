package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.Dependency;

import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents an answer to a migration request message. The node
 * which received the request specifies the dependencies of the requested item.
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

public class RequestItemAnswerMessage {
	
	public static final String MSG_KEY = "REQUEST_ITEM_ANSWER";
	
	private String conversationKey;
	private String type;
	private byte[] encodedPublicKey;
	
	// The list of required libraries id and version
	private ArrayList<Dependency> items;
	
	public RequestItemAnswerMessage(String conversationKey, byte[] encodedPublicKey) {
		setType(MSG_KEY);
		setConversationKey(conversationKey);
		items = new ArrayList<Dependency>();
		setEncodedPublicKey(encodedPublicKey);
	}
	
	public String getConversationKey() {
		return conversationKey;
	}

	public void setConversationKey(String conversationKey) {
		this.conversationKey = conversationKey;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void addItem(Dependency dependency) {
		boolean found = false;
		for (Dependency dependencyInList : items) {
			if (dependencyInList.getId().equals(dependency.getId())) {
				found = true;
				break;
			}
		}
		if (!found)
			items.add(dependency);
	}

	public byte[] getEncodedPublicKey() {
		return encodedPublicKey;
	}

	public void setEncodedPublicKey(byte[] encodedPublicKey) {
		this.encodedPublicKey = encodedPublicKey;
	}

	/**
	 * Method to obtain a JSON representation of this {@link RequestItemAnswerMessage}.
	 * 
	 * @return a JSON representation of this {@link RequestItemAnswerMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
