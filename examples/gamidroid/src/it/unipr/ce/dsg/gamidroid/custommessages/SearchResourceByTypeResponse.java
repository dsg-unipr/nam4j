package it.unipr.ce.dsg.gamidroid.custommessages;

import it.unipr.ce.dsg.s2p.centralized.utils.Resource;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * This class represents the answer to the {@link SearchResourceByType} custom
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
public class SearchResourceByTypeResponse {
	
	public static final String MSG_KEY = "GET_RESOURCE_BY_TYPE_RESPONSE";
	
	private Set<Resource> resources;
	
	public SearchResourceByTypeResponse() {
		resources = Collections.newSetFromMap(new ConcurrentHashMap<Resource, Boolean>());
	}

}
