package it.unipr.ce.dsg.nam4j.impl.mobility.xmlparser;

import it.unipr.ce.dsg.nam4j.impl.NetworkedAutonomicMachine.MigrationSubject;

/**
 * <p>
 * Class used by {@link SAXPArser} to parse the info element in the XML file
 * which describes an item (a {@link FunctionalModule} or a {@link Service}).
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

public class LibraryInformation {
	
	private String id;
	private String version;
	private MigrationSubject type;
	private String mainClass;
	
	public LibraryInformation() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public MigrationSubject getType() {
		return type;
	}

	public void setType(String type) {
		this.type = MigrationSubject.toMigrationSubject(type);
	}

	public String getMainClass() {
		return mainClass;
	}
	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

}
