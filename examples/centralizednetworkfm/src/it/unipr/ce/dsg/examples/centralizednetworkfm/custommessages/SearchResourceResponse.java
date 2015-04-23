package it.unipr.ce.dsg.examples.centralizednetworkfm.custommessages;

import it.unipr.ce.dsg.s2p.centralized.message.PostResourceMessage;
import it.unipr.ce.dsg.s2p.centralized.utils.Resource;
import it.unipr.ce.dsg.s2p.peer.PeerDescriptor;

import java.util.Set;

import com.google.gson.Gson;

/**
 * <p>
 * This class represents the answer to the {@link SearchResourceByLocation} custom
 * message used to search for resources by category, type and distance from the
 * user.
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
public class SearchResourceResponse {
	
	public static final String MSG_KEY = "GET_RESOURCE_RESPONSE";
	
	private String type;
	private PeerDescriptor peer;
	private Set<Resource> resources;
	
	public SearchResourceResponse(PeerDescriptor peer, Set<Resource> resources) {
		setType(MSG_KEY);
		setPeer(peer);
		setResources(resources);
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

	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
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
