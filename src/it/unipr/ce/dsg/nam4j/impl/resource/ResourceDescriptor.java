package it.unipr.ce.dsg.nam4j.impl.resource;

import it.unipr.ce.dsg.nam4j.interfaces.IResourceDescriptor;

/**
 * <p>
 * This class represents a resource descriptor.
 * </p>
 * 
 * <p>
 * Copyright (c) 2011, Distributed Systems Group, University of Parma, Italy.
 * Permission is granted to copy, distribute and/or modify this document under
 * the terms of the GNU Free Documentation License, Version 1.3 or any later
 * version published by the Free Software Foundation; with no Invariant
 * Sections, no Front-Cover Texts, and no Back-Cover Texts. A copy of the
 * license is included in the section entitled "GNU Free Documentation License".
 * </p>
 * 
 * @author Michele Amoretti (michele.amoretti@unipr.it)
 * @author Alessandro Grazioli (grazioli@ce.unipr.it)
 * 
 */

public class ResourceDescriptor implements IResourceDescriptor {

	String name = "ResourceDescriptor";
	String id = "ResourceId";

	/**
	 * Constructor.
	 */
	public ResourceDescriptor() {
	}

	/**
	 * Method to set the resource name.
	 *
	 * @param name
	 *            The resource name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method to get the resource name.
	 *
	 * @return the name of the resource
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method to set the resource id.
	 *
	 * @param id
	 *            The resource id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Method to get the resource id.
	 *
	 * @return the resource id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Method to compare two resource descriptors.
	 * 
	 * @param resourceDescriptor
	 *            The descriptor to be compared with the current one
	 * 
	 * @return true if the resourceDescriptor's id is equal to the one of the
	 *         caller
	 */
	public boolean equals(ResourceDescriptor resourceDescriptor) {
		return this.id.equals(resourceDescriptor.getId());
	}
}