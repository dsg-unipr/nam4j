package it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser;

/**
 * <p>
 * Class used by {@link SAXPArser} to parse {@link FunctionalModule} element in
 * the XML file which describes the library. Such an element exists only for
 * {@link Service}s and identifies the {@link FunctionalModule} to which it is
 * bound.
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

public class FunctionalModuleForService {
	
	private String version = null;
	private String id = null;
	
	public FunctionalModuleForService() {}
	
	public String getVersion() {
		return this.version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getId() {
		return this.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

}
