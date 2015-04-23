package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a message to request an action related to context
 * events (e.g. subscribe/unsubscribe to/from context event notifications).
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
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Nicholas Barbieri
 * @author Luca Barili
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ContextEventActionRequestMessage {
	
	public static final String MSG_KEY = "CONTEXT";
	
	private String type;
	private PeerDescriptor peer;
	private String info;
	private String infoType;
	private int hops;
	private String sender;
	
	public ContextEventActionRequestMessage(PeerDescriptor peer, String info, String type, int hops, String sender) {
		setType(MSG_KEY);
		setPeer(peer);
		setInfo(info);
		setInfoType(type);
		setHops(hops);
		setSender(sender);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PeerDescriptor getPeer() {
		return peer;
	}

	public void setPeer(PeerDescriptor peer) {
		this.peer = peer;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public int getHops() {
		return hops;
	}

	public void setHops(int hops) {
		this.hops = hops;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	/**
	 * Method to obtain a JSON representation of this {@link ContextEventActionRequestMessage}.
	 * 
	 * @return a JSON representation of this {@link ContextEventActionRequestMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
}
