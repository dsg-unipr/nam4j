package it.unipr.ce.dsg.nam4j.impl.messages;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.Dependency;
import it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser.MinimumRequirements;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import java.util.ArrayList;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents a message to request a MIGRATE mobility action. Such a
 * message requests to a node if it is available to receive an item (FM or
 * Service) and continue the execution.
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

public class RequestMigrateMessage {
	
	public static final String MSG_KEY = "REQUEST_MIGRATE";
	
	private String conversationKey;
	private Action action;
	private Platform platform;
	private String itemId;
	private String type;
	private PeerDescriptor peer;
	private String version;
	private MinimumRequirements minimumRequirements;
	
	// The role of the requested item, either Functional Module or Service
	private MigrationSubject role;
	
	// The list of dependencies id and version
	private ArrayList<Dependency> items = new ArrayList<>();
	
	public RequestMigrateMessage(String conversationKey, PeerDescriptor peer, Platform platform, String itemId, MigrationSubject role, Action action, ArrayList<Dependency> items, String version, MinimumRequirements minimumRequirements) {
		setType(MSG_KEY);
		setConversationKey(conversationKey);
		setPeer(peer);
		setVersion(version);
		setMinimumRequirements(minimumRequirements);
		setAction(action);
		setPlatform(platform);
		setItemId(itemId);
		setMigrationSubject(role);
		setItems(items);
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

	public MinimumRequirements getMinimumRequirements() {
		return minimumRequirements;
	}

	public void setMinimumRequirements(MinimumRequirements minimumRequirements) {
		this.minimumRequirements = minimumRequirements;
	}

	public ArrayList<Dependency> getItems() {
		return items;
	}

	public void setItems(ArrayList<Dependency> items) {
		for (Dependency item : items) {
			boolean found = false;
			for (Dependency dependency : this.items) {
				if (dependency.getId().equals(item.getId())) {
					found = true;
					break;
				}
			}
			if (!found)
				this.items.add(item);
		}
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
