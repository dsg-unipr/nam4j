package it.unipr.ce.dsg.nam4j.impl.mobility.utils;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Action;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;
import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.Platform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * <p>
 * This class represents a conversation between two peers.
 * </p>
 * 
 * <p>
 *  Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 *  Permission is granted to copy, distribute and/or modify this document
 *  under the terms of the GNU Free Documentation License, Version 1.3
 *  or any later version published by the Free Software Foundation;
 *  with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.
 *  A copy of the license is included in the section entitled "GNU
 *  Free Documentation License".
 * </p>
 * 
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ConversationItem {
	
	/** The id of the conversation */
	String id = null;
	
	/** The contact address of the partner in the conversation */
	String partnerContactAddress = null;
	
	/** The id of the item to be migrated */
	String itemId = null;
	
	/** The id of the item to be migrated */
	String itemVersion;
	
	/** The object to be migrated for state preservation */
	Object object = null;
	
	/** The mobility action */
	Action action = null;
	
	/** The role of the item to be migrated */
	MigrationSubject role;
	
	/** The platform of the addressee node */
	Platform platform;
	
	/** List of missing dependencies */
	HashMap<String, String> missingDependencies;

	public ConversationItem(String id, String partnerContactAddress, String itemId, Object object, String itemVersion, Action action, MigrationSubject role, Platform platform) {
		setId(id);
		setPartnerContactAddress(partnerContactAddress);
		setItemId(itemId);
		setItemVersion(itemVersion);
		setObject(object);
		setAction(action);
		setRole(role);
		setPlatform(platform);
		missingDependencies = new HashMap<String, String>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartnerContactAddress() {
		return partnerContactAddress;
	}

	public void setPartnerContactAddress(String partnerContactAddress) {
		this.partnerContactAddress = partnerContactAddress;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemVersion() {
		return itemVersion;
	}

	public void setItemVersion(String itemVersion) {
		this.itemVersion = itemVersion;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public MigrationSubject getRole() {
		return role;
	}

	public void setRole(MigrationSubject role) {
		this.role = role;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	
	public void addMissingDependencies(HashMap<String, String> list) {
		Iterator<Entry<String, String>> missingIt = list.entrySet().iterator();
		while(missingIt.hasNext()) {
			Entry<String, String> pairs = (Entry<String, String>) missingIt.next();
			this.missingDependencies.put(pairs.getKey(), pairs.getValue());
		}
	}
	
	public void removeMissingDependency(String id) {
		this.missingDependencies.remove(id);
	}
	
	public int getMissingDependenciesSize() {
		return this.missingDependencies.size();
	}
}
