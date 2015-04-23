package it.unipr.ce.dsg.examples.centralizednetworkfm.custommessages;

import com.google.gson.Gson;

import it.unipr.ce.dsg.s2p.centralized.message.PostResourceMessage;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

/**
 * <p>
 * This class represents a custom message used to search for resources by
 * category, type and distance from the user.
 * </p>
 * 
 * <p>
 * Copyright (c) 2013, Distributed Systems Group, University of Parma, Italy.
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
public class SearchResourceByType {
	
	public static final String MSG_KEY = "GET_RESOURCE_BY_TYPE";
	
	/** The type of the message */
	private String type;
	
	/** The category of the resource (e.g. sensor, info, file, ...) */
	private String resourceCategory;
	
	/**
	 * The type of the resource (e.g. if the category is sensor, the type can be
	 * temperature, noise, light, ...)
	 */
	private String resourceType;
	
	/** The sender of the message */
	private PeerDescriptor peer;
	
	public SearchResourceByType(PeerDescriptor peer, String rc, String resourceType) {
		setType(MSG_KEY);
		setPeer(peer);
		setResourceCategory(rc);
		setResourceType(resourceType);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResourceCategory() {
		return resourceCategory;
	}

	public void setResourceCategory(String resourceCategory) {
		this.resourceCategory = resourceCategory;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public PeerDescriptor getPeer() {
		return peer;
	}

	public void setPeer(PeerDescriptor peer) {
		this.peer = peer;
	}
	
	/**
	 * Method to obtain a JSON representation of this {@link PostResourceMessage}
	 * 
	 * @return a JSON representation of this {@link PutDataMessage}
	 */
	public String getJSONString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

}
