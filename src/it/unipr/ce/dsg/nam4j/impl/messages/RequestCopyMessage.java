package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a request message to obtain an item (FM or Service).
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

public class RequestCopyMessage {
	
	public static final String MSG_KEY = "REQUEST_ITEM";

	private String conversationKey;
	private Action action;
	private Platform platform;
	private String itemId;
	private String type;
	private PeerDescriptor peer;
	private String version;
	
	// The role of the requested item, either Functional Module or Service
	private MigrationSubject role;
	
	public RequestCopyMessage(String conversationKey, PeerDescriptor peer, Platform platform, String itemId, MigrationSubject role, Action action, String version) {
		setType(MSG_KEY);
		setConversationKey(conversationKey);
		setPeer(peer);
		setVersion(version);
		setAction(action);
		setPlatform(platform);
		setItemId(itemId);
		setMigrationSubject(role);
	}
	
	public String getConversationKey() {
		return conversationKey;
	}

	public void setConversationKey(String conversationKey) {
		this.conversationKey = conversationKey;
	}
	
	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MigrationSubject getMigrationSubject() {
		return role;
	}

	public void setMigrationSubject(MigrationSubject role) {
		this.role = role;
	}

	public PeerDescriptor getPeer() {
		return peer;
	}

	public void setPeer(PeerDescriptor peer) {
		this.peer = peer;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Method to obtain a JSON representation of this {@link RequestCopyMessage}.
	 * 
	 * @return a JSON representation of this {@link RequestCopyMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
