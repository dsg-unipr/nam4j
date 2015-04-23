package it.unipr.ce.dsg.gamidroid.custommessages;

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
	
	/**
	 * The user may be interested in the distance [m] between its position and
	 * the resource (i.e. find all sensors in a certain range).
	 * The parameter can be null.
	 */
	public int distance;
	
	/**
	 * The latitude and longitude of the user's position. Such values are used,
	 * together with the distance parameter, by the message receiver to check if
	 * its resources are located in the circle having the coordinates as center
	 * and the distance as radius.
	 */
	public double peerLat, peerLgt;
	
	/** The sender of the message */
	private PeerDescriptor peer;
	
	public SearchResourceByType(PeerDescriptor peer, String rc, String rt, int d, double lat, double lgt) {
		setType(MSG_KEY);
		setPeer(peer);
		setResourceCategory(rc);
		setResourceType(rt);
		setDistance(d);
		setPeerLat(lat);
		setPeerLgt(lgt);
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

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public double getPeerLat() {
		return peerLat;
	}

	public void setPeerLat(double peerLat) {
		this.peerLat = peerLat;
	}

	public double getPeerLgt() {
		return peerLgt;
	}

	public void setPeerLgt(double peerLgt) {
		this.peerLgt = peerLgt;
	}

	public PeerDescriptor getPeer() {
		return peer;
	}

	public void setPeer(PeerDescriptor peer) {
		this.peer = peer;
	}

}
